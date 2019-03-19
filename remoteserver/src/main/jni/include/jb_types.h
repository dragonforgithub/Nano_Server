/**************************************************************************************************
*  File Name	：jb_types.h
*  Describe	：系统中所有文件都需要使用的宏定义
*  Version	：1.0
*  Author		：
*  History		：		Date 		Athor 			Version 			Reason
*					-------------------- ---------------------- --------------- ---------------------
*
***************************************************************************************************/
#ifndef	JB_TYPES_H
#define	JB_TYPES_H

/*字节序*/
#define J_LITTLE_ENDIAN				(0)	/**< 低位在前*/
#define J_BIG_ENDIAN				(1)	/**< 高位在前*/

/*运营商*/
#define J_OPERATOR_HZH				(1)
#define J_OPERATOR_HEFEI			(2)
#define J_OPERATOR_WUHAN			(3)
#define J_OPERATOR_LAIXI			(4)

/*操作系统*/
#define J_LINUX						(0)
#define J_CYGWIN					(1)
#define J_UCOSII						(2)
#define J_OS20						(3)

/*硬件平台*/
#define J_PCEMU						(0)
#define J_HI2021						(1)
#define J_STI5100					(2)
#define J_STI5105					(3)
#define J_STI5516					(4)

/*开发环境*/
#define J_GNU						(0)
#define J_ST20						(1)
#define J_ST40						(2)
#define J_ARM						(3)

#define _nop_()

#ifndef J_BYTE_ORDER
#define J_BYTE_ORDER    J_LITTLE_ENDIAN
#endif

/*基本数据类型*/

typedef unsigned char		J_U8; 			/**< 8位无符号数*/
typedef unsigned short 	J_U16;			/**< 16位无符号数*/
typedef unsigned long		J_U32;			/**< 32位无符号数*/
typedef char				J_S8;			/**< 8位有符号数*/
typedef short				J_S16;  		/**< 8位有符号数*/
typedef long				J_S32;  		/**< 8位有符号数*/
typedef float				J_Float;		/**< 32位浮点型*/
typedef double			J_Double;		/**< 64位双精度型*/
typedef void*			J_Ptr;			/**< 通用指针*/
typedef int				J_Int;					/**< 整数*/
typedef unsigned int		J_UInt;			/**< 无符号整数*/
typedef long int			J_Size;			/**< 空间大小*/
typedef J_U8				J_BOOL;			/** @brief 布尔型*/


typedef J_S32 			ID;				/**< ID*/
typedef J_S32			Coord;			/**< 坐标*/

typedef J_S32			Time;			/**< 时间(秒为单位)*/
typedef J_S32			MSec;			/**< 毫秒*/
typedef J_S8				Char;			/**< 字符*/

typedef unsigned char		BYTE;
typedef unsigned short		WORD;
typedef unsigned long		DWORD;
typedef long				LONG;
typedef unsigned long		ULONG;


#ifndef NULL
#define NULL (void*)(0)					/**< 空指针*/
#endif

#ifndef TRUE
#define TRUE				(1)				/**< 逻辑真*/
#endif

#ifndef FALSE
#define FALSE			(0)				/**< 逻辑假*/
#endif

typedef 	J_S32			Handle;
typedef  J_S32	 		Result;


/*-----------------------------------------------------------------------------
Function Name:	*D_Func
Input		:	ptr: 数据指针
Output		:	pd: 调用参数
Return 		:	无
Describe		:	函数指针类型定义
-------------------------------------------------------------------------------*/
typedef void	(*Func)   (J_Ptr ptr, J_Ptr pd);

/*-----------------------------------------------------------------------------
Function Name:	*D_CmpFunc
Input		:	p1: 数据1 指针
Output		:	p2: 数据2 指针
Return 		:	无
Describe		:	当 @a p1 等于 @a p2 时,返回0。
				当 @a p1 小于 @a p2 时,返回一个负数。
				当 @a p1 大于 @a p2 时,返回一个正数。
-------------------------------------------------------------------------------*/
typedef J_S32 (*CmpFunc)(J_Ptr p1, J_Ptr p2);
typedef J_S32 (*CmpFunc2)(J_Ptr p1, J_Ptr p2, ID id);


#define J_INVALID_HANDLE			(-1)		/**< 无效句柄*/
#define J_TIMEOUT_INFINITE 			(-1) 	/**< 一直等待>*/
#define J_TIMEOUT_IMMEDIATE 		(0) 		/**< 立即返回>*/


//错误类型定义
#define J_OK							(0)		/**< 函数正确执行*/
#define J_ERR						(-1)		/**< 函数执行错误*/
#define J_ERR_FULL					(-2)		/**< 消息队列满错误*/
#define J_ERR_TIMEOUT				(-3)		/**< 超时错误*/
#define J_ERR_NO_SPACE				(-4)		/**< 无足够的内存空间*/
#define J_ERR_INVALID_HANDLE		(-5)		/**< 无效的句柄*/
#define J_ERR_INVALID_ARGUMENT	(-6)			/**< 参数无效*/
#define J_ERR_EMPTY					(-7)		/**< 消息队列中无数据*/
#define J_ERR_OVERFLOW				(-8)		/**< 缓冲区溢出*/
#define J_ERR_FAULT					(-9)		/**< 错误*/
#define J_ERR_NOT_SUPPORT			(-10)	/**< 功能现在不支持*/
#define J_ERR_IO						(-11)	/**< IO错误*/
#define J_ERR_NO_DEV				(-12)	/**< 没有设备*/
#define J_ERR_ALREADY_DONE			(-13)	/**< 已经做过此操作*/


#define TMR_CONTINUE				(0)		
#define TMR_ABORT					(-1)	
#define TMR_COMPLETE				(-2)
#define TMR_ONCE					(-3)

/** @brief 求 @a a 和 @a b 中的最大值*/
#define MAX(a,b)					((a)>(b)?(a):(b))

/** @brief 求 @a a 和 @a b 中的最小值*/
#define MIN(a,b)					((a)<(b)?(a):(b))

/** @brief 数值是否在范围内*/
#define IN_RANGE(a,l,b)			(((a)>=(l))&&((a)<=(b)))

/** @brief 在位操作数组中设置位*/
#define SET_MASK(m,n)			((m)[(n)>>3] |= (1<<((n)&7)))

/** @brief 在位操作数组中清除位*/
#define CLEAR_MASK(m,n)			((m)[(n)>>3] &= ~(1<<((n)&7)))

/** @brief 在位操作数组中检查一位是否已经设置设置*/
#define ISSET_MASK(m,n)			((m)[(n)>>3] & (1<<((n)&7)))

/** @brief 求 @a a 的绝对值*/
#define ABS(a)					((a)>(0)?(a):-(a))

/** @brief 宏定义块开始*/
#define MACRO_BEGIN			do {

/** @brief 宏定义块结束*/
#define MACRO_END				} while(0)

/*字节序变换函数*/
/** @brief 变换一个16位数的字节序*/
#define SWAP16(_d)				((((J_U16)(_d))<<8) | (((J_U16)(_d))>>8))

/** @brief 变换一个32位数的字节序*/
#define SWAP32(_d)\
								((((J_U32)(_d))<<24) |\
								((((J_U32)(_d))<<8)&0x00FF0000)|\
								((((J_U32)(_d))>>8)&0x0000FF00)|\
								(((J_U32)(_d))>>24))

#if (J_BYTE_ORDER == J_BIG_ENDIAN)

#define d_htons(_s)	(_s)
#define d_htonl(_l)	(_l)
#define d_ntohs(_s)	(_s)
#define d_ntohl(_l)	(_l)

#else

#define d_htons(_s)	SWAP16(_s)
#define d_htonl(_l)	SWAP16(_l)
#define d_ntohs(_s)	SWAP32(_s)
#define d_ntohl(_l)	SWAP32(_l)

#endif




#endif

