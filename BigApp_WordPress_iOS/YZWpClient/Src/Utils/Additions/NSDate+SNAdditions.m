//
//  NSDate+SNAdditions.m
//  SNFramework
//
//  Created by  liukun on 13-1-14.
//  Copyright (c) Suning. All rights reserved.
//

#import "NSDate+SNAdditions.h"

@implementation NSDate (SNAdditions)

+ (NSDate *)dateFromString:(NSString *)string withFormat:(NSString *)formatString
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:formatString];
    NSDate *date = [dateFormatter dateFromString:string];
    return date;
}

+ (NSString *)stringFromDate:(NSDate *)date withFormat:(NSString *)formatString
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:formatString];
    NSString *dateStr = [dateFormatter stringFromDate:date];
    return dateStr;
}

+ (NSUInteger)timeStamp
{
	NSTimeInterval time = [NSDate timeIntervalSinceReferenceDate];
	return (NSUInteger)(time * 1000.0f);
}

- (NSString *)stringWithDateFormat:(NSString *)format
{
    NSDateFormatter * dateFormatter = [[NSDateFormatter alloc] init];
	[dateFormatter setDateFormat:format];
	return [dateFormatter stringFromDate:self];
}

@end
