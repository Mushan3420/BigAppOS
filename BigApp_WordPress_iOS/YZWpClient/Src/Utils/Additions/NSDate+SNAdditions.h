//
//  NSDate+SNAdditions.h
//  SNFramework
//
//  Created by  liukun on 13-1-14.
//  Copyright (c) Suning. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSDate (SNAdditions)

+ (NSDate *)dateFromString:(NSString *)string withFormat:(NSString *)formatString;

+ (NSString *)stringFromDate:(NSDate *)date withFormat:(NSString *)formatString;

+ (NSUInteger)timeStamp;

- (NSString *)stringWithDateFormat:(NSString *)format;

@end
