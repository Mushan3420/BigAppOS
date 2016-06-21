//
//  NSData+Base64.h
//  SNFramework
//
//  Created by  liukun on 13-1-9.
//  Copyright (c) Suning. All rights reserved.
//
// http://www.cocoadev.com/index.pl?BaseSixtyFour

#import <Foundation/Foundation.h>

@interface NSData (SNAdditions)

- (NSString *)MD5EncodedString;
- (NSData *)HMACSHA1EncodedDataWithKey:(NSString *)key;

+ (id)dataWithBase64EncodedString:(NSString *)string;
- (NSString *)base64EncodedString;

@end
