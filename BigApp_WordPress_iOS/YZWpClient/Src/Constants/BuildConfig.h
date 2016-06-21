//
//  BuildConfig.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//  开发环境配置

#ifndef YZWpClient_BuildConfig_h
#define YZWpClient_BuildConfig_h

#define TARGET_ENV_DEV 1
#define TARGET_ENV_SIT 0
#define TARGET_ENV_PRE 0
#define TARGET_ENV_PRD 0


//#define DEBUGLOG 1

#ifdef  DEBUGLOG
#       define DLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
#else
#       define DLog(...)
#endif



/**************************************快捷函数**************************************/

#undef RGBCOLOR
#define RGBCOLOR(r,g,b)     [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1]

#undef  RGBACOLOR
#define RGBACOLOR(r,g,b,a) [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:a]

#undef IsNilOrNull
#define IsNilOrNull(_ref)   (((_ref) == nil) || ([(_ref) isEqual:[NSNull null]]))

#define kSCREEN_WIDTH  [UIScreen mainScreen].bounds.size.width
#define kSCREEN_HEIGHT [UIScreen mainScreen].bounds.size.height

#define AutoDeviceWidth(kWidth)  ((kWidth/(750/2.0)) * ([UIScreen mainScreen].bounds.size.width))
#define AutoDeviceHeight(kHeight)  ((kHeight/(1334/2.0)) * ([UIScreen mainScreen].bounds.size.height))

/**************************************字体设置**************************************/

#define kChineseFontName          @"STHeitiSC-Light"

#define kChineseBoldFontName      @"STHeitiSC-Medium"

#define kChineseFontNameXi        @"STHeitiSC-LightXi"



#define TPTextFiledContentColor    RGBCOLOR(102, 102, 102)



/**************************************国际化语言设置**************************************/
#define LocalizedString(_str)   NSLocalizedString(_str, @"")


#define kPagesCount   @"10"



#endif
