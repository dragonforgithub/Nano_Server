/**************************************************************************************************
*  File Name	��jb_types.h
*  Describe	��ϵͳ�������ļ�����Ҫʹ�õĺ궨��
*  Version	��1.0
*  Author		��
*  History		��		Date 		Athor 			Version 			Reason
*					-------------------- ---------------------- --------------- ---------------------
*
***************************************************************************************************/
#ifndef	JB_TYPES_H
#define	JB_TYPES_H

/*�ֽ���*/
#define J_LITTLE_ENDIAN				(0)	/**< ��λ��ǰ*/
#define J_BIG_ENDIAN				(1)	/**< ��λ��ǰ*/

/*��Ӫ��*/
#define J_OPERATOR_HZH				(1)
#define J_OPERATOR_HEFEI			(2)
#define J_OPERATOR_WUHAN			(3)
#define J_OPERATOR_LAIXI			(4)

/*����ϵͳ*/
#define J_LINUX						(0)
#define J_CYGWIN					(1)
#define J_UCOSII						(2)
#define J_OS20						(3)

/*Ӳ��ƽ̨*/
#define J_PCEMU						(0)
#define J_HI2021						(1)
#define J_STI5100					(2)
#define J_STI5105					(3)
#define J_STI5516					(4)

/*��������*/
#define J_GNU						(0)
#define J_ST20						(1)
#define J_ST40						(2)
#define J_ARM						(3)

#define _nop_()

#ifndef J_BYTE_ORDER
#define J_BYTE_ORDER    J_LITTLE_ENDIAN
#endif

/*������������*/

typedef unsigned char		J_U8; 			/**< 8λ�޷�����*/
typedef unsigned short 	J_U16;			/**< 16λ�޷�����*/
typedef unsigned long		J_U32;			/**< 32λ�޷�����*/
typedef char				J_S8;			/**< 8λ�з�����*/
typedef short				J_S16;  		/**< 8λ�з�����*/
typedef long				J_S32;  		/**< 8λ�з�����*/
typedef float				J_Float;		/**< 32λ������*/
typedef double			J_Double;		/**< 64λ˫������*/
typedef void*			J_Ptr;			/**< ͨ��ָ��*/
typedef int				J_Int;					/**< ����*/
typedef unsigned int		J_UInt;			/**< �޷�������*/
typedef long int			J_Size;			/**< �ռ��С*/
typedef J_U8				J_BOOL;			/** @brief ������*/


typedef J_S32 			ID;				/**< ID*/
typedef J_S32			Coord;			/**< ����*/

typedef J_S32			Time;			/**< ʱ��(��Ϊ��λ)*/
typedef J_S32			MSec;			/**< ����*/
typedef J_S8				Char;			/**< �ַ�*/

typedef unsigned char		BYTE;
typedef unsigned short		WORD;
typedef unsigned long		DWORD;
typedef long				LONG;
typedef unsigned long		ULONG;


#ifndef NULL
#define NULL (void*)(0)					/**< ��ָ��*/
#endif

#ifndef TRUE
#define TRUE				(1)				/**< �߼���*/
#endif

#ifndef FALSE
#define FALSE			(0)				/**< �߼���*/
#endif

typedef 	J_S32			Handle;
typedef  J_S32	 		Result;


/*-----------------------------------------------------------------------------
Function Name:	*D_Func
Input		:	ptr: ����ָ��
Output		:	pd: ���ò���
Return 		:	��
Describe		:	����ָ�����Ͷ���
-------------------------------------------------------------------------------*/
typedef void	(*Func)   (J_Ptr ptr, J_Ptr pd);

/*-----------------------------------------------------------------------------
Function Name:	*D_CmpFunc
Input		:	p1: ����1 ָ��
Output		:	p2: ����2 ָ��
Return 		:	��
Describe		:	�� @a p1 ���� @a p2 ʱ,����0��
				�� @a p1 С�� @a p2 ʱ,����һ��������
				�� @a p1 ���� @a p2 ʱ,����һ��������
-------------------------------------------------------------------------------*/
typedef J_S32 (*CmpFunc)(J_Ptr p1, J_Ptr p2);
typedef J_S32 (*CmpFunc2)(J_Ptr p1, J_Ptr p2, ID id);


#define J_INVALID_HANDLE			(-1)		/**< ��Ч���*/
#define J_TIMEOUT_INFINITE 			(-1) 	/**< һֱ�ȴ�>*/
#define J_TIMEOUT_IMMEDIATE 		(0) 		/**< ��������>*/


//�������Ͷ���
#define J_OK							(0)		/**< ������ȷִ��*/
#define J_ERR						(-1)		/**< ����ִ�д���*/
#define J_ERR_FULL					(-2)		/**< ��Ϣ����������*/
#define J_ERR_TIMEOUT				(-3)		/**< ��ʱ����*/
#define J_ERR_NO_SPACE				(-4)		/**< ���㹻���ڴ�ռ�*/
#define J_ERR_INVALID_HANDLE		(-5)		/**< ��Ч�ľ��*/
#define J_ERR_INVALID_ARGUMENT	(-6)			/**< ������Ч*/
#define J_ERR_EMPTY					(-7)		/**< ��Ϣ������������*/
#define J_ERR_OVERFLOW				(-8)		/**< ���������*/
#define J_ERR_FAULT					(-9)		/**< ����*/
#define J_ERR_NOT_SUPPORT			(-10)	/**< �������ڲ�֧��*/
#define J_ERR_IO						(-11)	/**< IO����*/
#define J_ERR_NO_DEV				(-12)	/**< û���豸*/
#define J_ERR_ALREADY_DONE			(-13)	/**< �Ѿ������˲���*/


#define TMR_CONTINUE				(0)		
#define TMR_ABORT					(-1)	
#define TMR_COMPLETE				(-2)
#define TMR_ONCE					(-3)

/** @brief �� @a a �� @a b �е����ֵ*/
#define MAX(a,b)					((a)>(b)?(a):(b))

/** @brief �� @a a �� @a b �е���Сֵ*/
#define MIN(a,b)					((a)<(b)?(a):(b))

/** @brief ��ֵ�Ƿ��ڷ�Χ��*/
#define IN_RANGE(a,l,b)			(((a)>=(l))&&((a)<=(b)))

/** @brief ��λ��������������λ*/
#define SET_MASK(m,n)			((m)[(n)>>3] |= (1<<((n)&7)))

/** @brief ��λ�������������λ*/
#define CLEAR_MASK(m,n)			((m)[(n)>>3] &= ~(1<<((n)&7)))

/** @brief ��λ���������м��һλ�Ƿ��Ѿ���������*/
#define ISSET_MASK(m,n)			((m)[(n)>>3] & (1<<((n)&7)))

/** @brief �� @a a �ľ���ֵ*/
#define ABS(a)					((a)>(0)?(a):-(a))

/** @brief �궨��鿪ʼ*/
#define MACRO_BEGIN			do {

/** @brief �궨������*/
#define MACRO_END				} while(0)

/*�ֽ���任����*/
/** @brief �任һ��16λ�����ֽ���*/
#define SWAP16(_d)				((((J_U16)(_d))<<8) | (((J_U16)(_d))>>8))

/** @brief �任һ��32λ�����ֽ���*/
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

