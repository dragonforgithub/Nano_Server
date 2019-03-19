#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <errno.h>
#include <poll.h>

#include <android/log.h>

/*socket include*/
#include <sys/types.h>	/* See NOTES */
#include <sys/socket.h>
/*struct sockaddr_in asociation*/
#include <netinet/in.h>
#include <net/if.h>
#include <net/if_arp.h>
#include <arpa/inet.h>

#include <osi.h>
#include <socket_local.h>
#include <sockets.h>


#include "nano_socket.h"

#define LOG_TAG "nano-socket"

#define ANDROID_DLOG(format,...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG, format,__VA_ARGS__)

static J_Int	gSendSocket = J_INVALID_HANDLE;
static J_Int    gRcvSocket  = J_INVALID_HANDLE;

/*-----------------------------------------------------------------------------
Function Name:     Nano_StreamSend
Input            	:
Output          	:
Return          	:
Describe       	: 作为客户端发送数据
-------------------------------------------------------------------------------*/
void Nano_StreamSend(J_S8* stream,J_Int size)
{
    char* sName = "nano_socket_c2s";
	//char* sName = "nano_socket_c2s_nvoice";
	int sendLen = 0;

RETRY:
	/*（1）创建client socket */
	if(gSendSocket < 0){	
		gSendSocket = nano_socket_local_client(sName,ANDROID_SOCKET_NAMESPACE_ABSTRACT, SOCK_DGRAM);
		if(gSendSocket < 0){
			ANDROID_DLOG("errno %s\n",strerror(errno));
			return;
		}else{
			ANDROID_DLOG("create gSendSocket = %p\n",gSendSocket);
		}
	}
			
	/*（2）发送数据到server端 */	
	sendLen = write(gSendSocket, stream, size);
	if(sendLen < 0){
		ANDROID_DLOG("errno %s\n",strerror(errno));
		close(gSendSocket);
		gSendSocket=J_INVALID_HANDLE;
		goto RETRY;
	}else
		ANDROID_DLOG("Send(%d) : {%s}\n",sendLen,stream);
}

/*-----------------------------------------------------------------------------
Function Name:     Nano_StreamReceive
Input            	:
Output          	:
Return          	:
Describe       	: 作为主机端接收数据
-------------------------------------------------------------------------------*/
J_Int Nano_StreamReceive(J_U8* stream, J_Int size)
{
    J_S8* cName    = "nano_socket_s2c";
    //J_S8* cName    = "nano_socket_s2c_nvoice";
	J_Int ret,rcvLen = 0;

    struct pollfd pfds[1];
	
	/*（1）创建client socket接收server端数据*/
	if(gRcvSocket < 0){
		gRcvSocket = nano_socket_local_server(cName,ANDROID_SOCKET_NAMESPACE_ABSTRACT,SOCK_DGRAM);
		if(gRcvSocket < 0){
			ANDROID_DLOG("errno %s\n",strerror(errno));
			return rcvLen;
		}else{
			ANDROID_DLOG("create gRcvSocket = %p\n",gRcvSocket);
		}
	}

    pfds[0].fd = gRcvSocket;
    pfds[0].events = POLLIN;

    ret = poll(pfds, 1, 500);
    if (ret < 0) {
        ANDROID_DLOG("Cannot poll for fds: %s\n", strerror(errno));
        return rcvLen;
    }

    if (pfds[0].revents & POLLIN) {
        /*（2）读取数据*/
        rcvLen = read(gRcvSocket, stream, size);
        if(rcvLen < 0){
            ANDROID_DLOG("errno %s\n",strerror(errno));
            close(gRcvSocket);
            gRcvSocket = J_INVALID_HANDLE;
            rcvLen=0;
        }else{
            ANDROID_DLOG("Recv {%s}\n",stream);
        }
    }
	return rcvLen;
}

/*-----------------------------------------------------------------------------
Function Name:  	NanoOpen
Input           :	
Output         	:
Return         	:
Describe      	:	开启socket传输
-------------------------------------------------------------------------------*/
unsigned char  Nano_Open(){
	ANDROID_DLOG("NanoOpen : %d",0);

	/*
	if(gSendSocket >= 0){
		close(gSendSocket);
		gSendSocket  = J_INVALID_HANDLE;
	}

	if(gRcvSocket >= 0){
		close(gRcvSocket);
		gRcvSocket  = J_INVALID_HANDLE;
	}
	*/

	return 0;
}

/*-----------------------------------------------------------------------------
Function Name:  	NanoPollEvent
Input           :	
Output         	:
Return         	:
Describe      	:	应用轮询UDP事件变化，回调到客户端
-------------------------------------------------------------------------------*/
Event_T	Nano_PollEvent(unsigned char* dataBuf, int size){
	
	Event_T cmdType = EVENT_UNKNOWN;
	J_Int 	rcv_len = 0;
	J_U8 	rcv_data[32] = {0};
	
	rcv_len=Nano_StreamReceive(rcv_data,size);
    if(rcv_len>0){
        cmdType = (Event_T) (rcv_data[0] - '0');  //convert to int type
        strcpy((char *) dataBuf, (const char *) (rcv_data + 1)); //get data part
        ANDROID_DLOG("Nano_PollEvent need  %d , get %d : type[%d]-{%s}",size,rcv_len,cmdType,dataBuf);
    }
    return cmdType;
}

/*-----------------------------------------------------------------------------
Function Name:  	NanosetSpeakerOn
Input           	:	
Output         	:
Return         	:
Describe      	:	通知手柄切换通道
-------------------------------------------------------------------------------*/
unsigned char  Nano_setSpeakerOn(unsigned char state){
	J_S8  append_type[128]={0};

    sprintf(append_type,"%d%d",EVENT_SETTVSPEAKERON,state);
	ANDROID_DLOG("Nano_setSpeakerOn : %s",append_type+1);
	
	Nano_StreamSend(append_type,strlen(append_type));
	return 0;
}

/*-----------------------------------------------------------------------------
Function Name:  	NanodialCall
Input           	:	
Output         	:
Return         	:
Describe      	:	通知手柄发起呼叫
-------------------------------------------------------------------------------*/
unsigned char  Nano_dialCall(char* phoneNumber){
	J_S8  append_type[128]={0};
	
	sprintf(append_type,"%d",EVENT_DIALTVCALL);
	strcat(append_type,phoneNumber);

	Nano_StreamSend(append_type,strlen(append_type));
    ANDROID_DLOG("Nano_dialCall : %s",append_type+1);
	return 0;
}

/*-----------------------------------------------------------------------------
Function Name:  	NanoincomingCall
Input           	:	
Output         	:
Return         	:
Describe      	:	通知手柄来电振铃
-------------------------------------------------------------------------------*/
unsigned char  Nano_incomingCall(char* phoneNumber){
	J_S8  append_type[128]={0};
	
	sprintf(append_type,"%d",EVENT_INCOMINGCALL);
	strcat(append_type,phoneNumber);

	Nano_StreamSend(append_type,strlen(append_type));
    ANDROID_DLOG("Nano_incomingCall : %s",append_type+1);
	return 0;
}

/*-----------------------------------------------------------------------------
Function Name:  	NanoanswerCall
Input           	:	
Output         	:
Return         	:
Describe      	:	通知手柄接听来电,停止震动
-------------------------------------------------------------------------------*/
unsigned char  Nano_answerCall(char* phoneNumber){
	J_S8  append_type[128]={0};
	
	sprintf(append_type,"%d",EVENT_ANSWERTVCALL);
	strcat(append_type,phoneNumber);

	Nano_StreamSend(append_type,strlen(append_type));
    ANDROID_DLOG("Nano_answerCall : %s",append_type+1);
	return 0;
}

/*-----------------------------------------------------------------------------
Function Name:  	NanohangupCall
Input           	:	
Output         	:
Return         	:
Describe      	:	通知手柄挂断通话
-------------------------------------------------------------------------------*/
unsigned char  Nano_hangupCall(char* phoneNumber){
	J_S8  append_type[128]={0};
	
	sprintf(append_type,"%d",EVENT_HANGUPTVCALL);
	strcat(append_type,phoneNumber);

	Nano_StreamSend(append_type,strlen(append_type));
    ANDROID_DLOG("Nano_hangupCall : %s",append_type+1);
	return 0;
}

