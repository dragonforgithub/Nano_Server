#ifndef _NANO_SOCKET_H_
#define _NANO_SOCKET_H_

#include <jb_types.h>

typedef enum {
    EVENT_ONSUCCESS,
    EVENT_SETTVSPEAKERON,
    EVENT_ANSWERTVCALL,
    EVENT_HANGUPTVCALL,
    EVENT_DIALTVCALL,
    EVENT_INCOMINGCALL,
    EVENT_SENDDTMF,
    EVENT_UNKNOWN,
} Event_T;


extern unsigned char  Nano_Open();
extern Event_T        Nano_PollEvent(unsigned char* dataBuf, int size);
extern unsigned char  Nano_setSpeakerOn(unsigned char state);
extern unsigned char  Nano_dialCall(char* phoneNumber);
extern unsigned char  Nano_incomingCall(char* phoneNumber);
extern unsigned char  Nano_answerCall(char* phoneNumber);
extern unsigned char  Nano_hangupCall(char* phoneNumber);

#endif




