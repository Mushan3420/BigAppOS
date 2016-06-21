//
//  NSString+SNAdditions.h
//  SNFramework
//
//  Created by  liukun on 13-1-9.
//  Copyright (c) Suning. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (SNAdditions)

- (NSArray *)allURLs;

- (NSString *)urlByAppendingDict:(NSDictionary *)params;
- (NSString *)urlByAppendingDictNoEncode:(NSDictionary *)params;

- (NSString *)urlByAppendingArray:(NSArray *)params;
- (NSString *)urlByAppendingKeyValues:(id)first, ...;

- (NSString *)queryStringFromDictionary:(NSDictionary *)dict;
- (NSString *)queryStringFromArray:(NSArray *)array;
- (NSString *)queryStringFromKeyValues:(id)first, ...;

- (NSString *)URLEncoding;
- (NSString *)URLDecoding;

- (NSString *)MD5;
- (NSString *)trim;

- (BOOL)empty;
- (BOOL)notEmpty;

- (BOOL)eq:(NSString *)other;

- (BOOL)isValueOf:(NSArray *)array;
- (BOOL)isValueOf:(NSArray *)array caseInsens:(BOOL)caseInsens;


- (BOOL)isGetter;
- (BOOL)isSetter;
- (NSString *)getterToSetter;
- (NSString *)setterToGetter;


+ (NSString *)encryptUseDES:(NSString *)clearText key:(NSString *)key;
+ (NSString *)decryptUseDES:(NSString *)plainText key:(NSString *)key;

- (id)objectFromJSONString;
- (NSString *)formatJSON;

+ (NSString *)hexStringOfData:(NSData *)data;
+ (NSData *)dataOfHexString:(NSString *)hexString;

- (NSString *)MD5EncodedString;
- (NSData *)HMACSHA1EncodedDataWithKey:(NSString *)key;
- (NSString *)base64EncodedString;

+ (NSString *)GUIDString;

@end
