//
//  NSData+Base64.h
//  SNFramework
//
//  Created by  liukun on 13-1-9.
//  Copyright (c) Suning. All rights reserved.
//
// http://www.cocoadev.com/index.pl?BaseSixtyFour

#import "NSData+SNAdditions.h"
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonHMAC.h>
#import "GTMBase64.h"

@implementation NSData (SNAdditions)

+ (id)dataWithBase64EncodedString:(NSString *)string;
{
	return [GTMBase64 decodeString:string];
}

- (NSString *)MD5EncodedString
{
	unsigned char result[CC_MD5_DIGEST_LENGTH];
	CC_MD5([self bytes], (CC_LONG)[self length], result);
	
	return [NSString stringWithFormat:@"%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X",
			result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
			result[8], result[9], result[10], result[11], result[12], result[13], result[14], result[15]];
}

- (NSData *)HMACSHA1EncodedDataWithKey:(NSString *)key
{
	NSData *keyData = [key dataUsingEncoding:NSUTF8StringEncoding];
    void *buffer = malloc(CC_SHA1_DIGEST_LENGTH);
    CCHmac(kCCHmacAlgSHA1, [keyData bytes], [keyData length], [self bytes], [self length], buffer);
	
	NSData *encodedData = [NSData dataWithBytesNoCopy:buffer length:CC_SHA1_DIGEST_LENGTH freeWhenDone:YES];
    return encodedData;
}

- (NSString *)base64EncodedString
{
	return [GTMBase64 stringByEncodingData:self];
}
@end