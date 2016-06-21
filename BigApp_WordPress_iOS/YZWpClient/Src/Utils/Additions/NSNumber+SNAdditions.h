//
//  NSNumber+SNAdditions.h
//  SNFramework
//
//  Created by  liukun on 13-1-26.
//  Copyright (c) Suning. All rights reserved.
//

#import <Foundation/Foundation.h>

#undef	__INT
#define __INT( __x )			[NSNumber numberWithInt:(NSInteger)__x]

#undef	__UINT
#define __UINT( __x )			[NSNumber numberWithUnsignedInt:(NSUInteger)__x]

#undef	__FLOAT
#define	__FLOAT( __x )			[NSNumber numberWithFloat:(float)__x]

#undef	__DOUBLE
#define	__DOUBLE( __x )			[NSNumber numberWithDouble:(double)__x]

#pragma mark -


@interface NSNumber (SNAdditions)

@end
