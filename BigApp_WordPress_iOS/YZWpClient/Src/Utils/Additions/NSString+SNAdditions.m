//
//  NSString+SNAdditions.m
//  SNFramework
//
//  Created by  liukun on 13-1-9.
//  Copyright (c) Suning. All rights reserved.
//

#import "NSString+SNAdditions.h"
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonCryptor.h>
#import "NSData+SNAdditions.h"


@implementation NSString (SNAdditions)

- (NSArray *)allURLs
{
	NSMutableArray * array = [NSMutableArray array];
	
	NSInteger stringIndex = 0;
	while ( stringIndex < self.length )
	{
		NSRange searchRange = NSMakeRange(stringIndex, self.length - stringIndex);
		NSRange httpRange = [self rangeOfString:@"http://" options:NSCaseInsensitiveSearch range:searchRange];
		NSRange httpsRange = [self rangeOfString:@"https://" options:NSCaseInsensitiveSearch range:searchRange];
		
		NSRange startRange;
		if ( httpRange.location == NSNotFound )
		{
			startRange = httpsRange;
		}
		else if ( httpsRange.location == NSNotFound )
		{
			startRange = httpRange;
		}
		else
		{
			startRange = (httpRange.location < httpsRange.location) ? httpRange : httpsRange;
		}
		
		if (startRange.location == NSNotFound)
		{
			break;
		}
		else
		{
			NSRange beforeRange = NSMakeRange( searchRange.location, startRange.location - searchRange.location );
			if ( beforeRange.length )
			{
				//				NSString * text = [string substringWithRange:beforeRange];
				//				[array addObject:text];
			}
			
			NSRange subSearchRange = NSMakeRange(startRange.location, self.length - startRange.location);
			NSRange endRange = [self rangeOfString:@" " options:NSCaseInsensitiveSearch range:subSearchRange];
			if ( endRange.location == NSNotFound)
			{
				NSString * url = [self substringWithRange:subSearchRange];
				[array addObject:url];
				break;
			}
			else
			{
				NSRange URLRange = NSMakeRange(startRange.location, endRange.location - startRange.location);
				NSString * url = [self substringWithRange:URLRange];
				[array addObject:url];
				
				stringIndex = endRange.location;
			}
		}
	}
	
	return array;
}

- (NSString *)queryStringFromDictionary:(NSDictionary *)dict
{
    NSMutableArray * pairs = [NSMutableArray array];
	for ( NSString * key in [dict keyEnumerator] )
	{
        id value = [dict valueForKey:key];
        if ([value isKindOfClass:[NSNumber class]])
        {
            value = [value stringValue];
        }
        else if ([value isKindOfClass:[NSString class]])
        {
            
        }
        else
        {
            continue;
        }
		
		NSString * urlEncoding = [value URLEncoding];
		[pairs addObject:[NSString stringWithFormat:@"%@=%@", key, urlEncoding]];
	}
	
	return [pairs componentsJoinedByString:@"&"];
}

- (NSString *)urlByAppendingDict:(NSDictionary *)params
{
    NSURL * parsedURL = [NSURL URLWithString:self];
	NSString * queryPrefix = parsedURL.query ? @"&" : @"?";
	NSString * query = [self queryStringFromDictionary:params];
	return [NSString stringWithFormat:@"%@%@%@", self, queryPrefix, query];
}

- (NSString *)urlByAppendingDictNoEncode:(NSDictionary *)params
{
    if (params==nil || [params isEqual:[NSNull null]])
    {
        return self;
    }
    
    NSURL *parsedURL = [NSURL URLWithString:self];
    NSString *queryPrefix = parsedURL.query ? @"&" : @"?";
    
    NSMutableArray * pairs = [NSMutableArray array];
    for ( NSString * key in [params keyEnumerator] )
    {
        id value = [params valueForKey:key];
        if ([value isKindOfClass:[NSNumber class]])
        {
            value = [value stringValue];
        }
        else if ([value isKindOfClass:[NSString class]])
        {
            
        }
        else
        {
            continue;
        }
        
        [pairs addObject:[NSString stringWithFormat:@"%@=%@", key, value]];
    }
    
    NSString *query = [pairs componentsJoinedByString:@"&"];
    
    return [NSString stringWithFormat:@"%@%@%@", self, queryPrefix, query];
}

- (NSString *)queryStringFromArray:(NSArray *)array
{
	NSMutableArray *pairs = [NSMutableArray array];
	
	for ( NSUInteger i = 0; i < [array count]; i += 2 )
	{
		NSObject * obj1 = [array objectAtIndex:i];
		NSObject * obj2 = [array objectAtIndex:i + 1];
		
		NSString * key = nil;
		NSString * value = nil;
		
		if ( [obj1 isKindOfClass:[NSNumber class]] )
		{
			key = [(NSNumber *)obj1 stringValue];
		}
		else if ( [obj1 isKindOfClass:[NSString class]] )
		{
			key = (NSString *)obj1;
		}
		else
		{
			continue;
		}
		
		if ( [obj2 isKindOfClass:[NSNumber class]] )
		{
			value = [(NSNumber *)obj2 stringValue];
		}
		else if ( [obj1 isKindOfClass:[NSString class]] )
		{
			value = (NSString *)obj2;
		}
		else
		{
			continue;
		}
		
		NSString * urlEncoding = [value URLEncoding];
		[pairs addObject:[NSString stringWithFormat:@"%@=%@", key, urlEncoding]];
	}
	
	return [pairs componentsJoinedByString:@"&"];
}

- (NSString *)urlByAppendingArray:(NSArray *)params
{
    NSURL * parsedURL = [NSURL URLWithString:self];
	NSString * queryPrefix = parsedURL.query ? @"&" : @"?";
	NSString * query = [self queryStringFromArray:params];
	return [NSString stringWithFormat:@"%@%@%@", self, queryPrefix, query];
}

- (NSString *)urlByAppendingKeyValues:(id)first, ...
{
	NSMutableDictionary * dict = [NSMutableDictionary dictionary];
	
	va_list args;
	va_start( args, first );
	
	for ( ;; )
	{
		NSObject<NSCopying> * key = [dict count] ? va_arg( args, NSObject * ) : first;
		if ( nil == key )
			break;
		
		NSObject * value = va_arg( args, NSObject * );
		if ( nil == value )
			break;
        
		[dict setObject:value forKey:key];
	}
    
	return [self urlByAppendingDict:dict];
}

- (NSString *)queryStringFromKeyValues:(id)first, ...
{
	NSMutableDictionary * dict = [NSMutableDictionary dictionary];
	
	va_list args;
	va_start( args, first );
	
	for ( ;; )
	{
		NSObject<NSCopying> * key = [dict count] ? va_arg( args, NSObject * ) : first;
		if ( nil == key )
			break;
		
		NSObject * value = va_arg( args, NSObject * );
		if ( nil == value )
			break;
		
		[dict setObject:value forKey:key];
	}
    
	return [self queryStringFromDictionary:dict];
}

- (BOOL)empty
{
	return [self length] > 0 ? NO : YES;
}

- (BOOL)notEmpty
{
	return [self length] > 0 ? YES : NO;
}

- (BOOL)eq:(NSString *)other
{
	return [self isEqualToString:other];
}

- (BOOL)isValueOf:(NSArray *)array
{
	return [self isValueOf:array caseInsens:NO];
}

- (BOOL)isValueOf:(NSArray *)array caseInsens:(BOOL)caseInsens
{
	NSStringCompareOptions option = caseInsens ? NSCaseInsensitiveSearch : 0;
	
	for ( NSObject * obj in array )
	{
		if ( NO == [obj isKindOfClass:[NSString class]] )
			continue;
		
		if ( [(NSString *)obj compare:self options:option] )
			return YES;
	}
	
	return NO;
}

- (NSString *)URLEncoding
{
	NSString * result = (NSString *)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes( kCFAllocatorDefault,
																			(CFStringRef)self,
																			NULL,
																			(CFStringRef)@"!*'();:@&=+$,/?%#[]",
																			kCFStringEncodingUTF8 ));
	return result;
}

- (NSString *)URLDecoding
{
	NSMutableString * string = [NSMutableString stringWithString:self];
    [string replaceOccurrencesOfString:@"+"
							withString:@" "
							   options:NSLiteralSearch
								 range:NSMakeRange(0, [string length])];
    return [string stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
}

- (NSString *)MD5
{
	const char* str = [self UTF8String];
    unsigned char result[CC_MD5_DIGEST_LENGTH];
    CC_MD5(str, (CC_LONG)strlen(str), result);
    
    NSMutableString *ret = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH*2];
    for(int i = 0; i<CC_MD5_DIGEST_LENGTH; i++) {
        [ret appendFormat:@"%02x",result[i]];
    }
    return ret;
}

- (NSString *)trim
{
	return [self stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
}

- (BOOL)isGetter {
    
    return ![self isSetter];
}


- (BOOL)isSetter {
    
    return [self hasPrefix:@"set"];
}


- (NSString *)getterToSetter {
    
    NSRange firstChar, rest;
    firstChar.location  = 0;
    firstChar.length    = 1;
    rest.location       = 1;
    rest.length         = self.length - 1;
    
    return [NSString stringWithFormat:@"set%@%@:",
            [[self substringWithRange:firstChar] uppercaseString],
            [self substringWithRange:rest]];
}


- (NSString *)setterToGetter {
    
    NSRange firstChar, rest;
    firstChar.location  = 3;
    firstChar.length    = 1;
    rest.location       = 4;
    rest.length         = self.length - 5;
    
    return [NSString stringWithFormat:@"%@%@",
            [[self substringWithRange:firstChar] lowercaseString],
            [self substringWithRange:rest]];
}

#pragma mark -
#pragma mark DES

+ (NSString *)doCipher:(NSString *)sTextIn key:(NSString *)sKey context:(CCOperation)encryptOrDecrypt
{
	NSStringEncoding EnC = NSUTF8StringEncoding;
    
    NSMutableData * dTextIn;
    if (encryptOrDecrypt == kCCDecrypt)
	{
        dTextIn = [[NSData dataWithBase64EncodedString:sTextIn] mutableCopy];
    }
    else
	{
        dTextIn = [[sTextIn dataUsingEncoding: EnC] mutableCopy];
    }
    NSMutableData * dKey = [[sKey dataUsingEncoding:EnC] mutableCopy];
    [dKey setLength:kCCBlockSizeDES];
    uint8_t *bufferPtr1 = NULL;
    size_t bufferPtrSize1 = 0;
    size_t movedBytes1 = 0;
    //uint8_t iv[kCCBlockSizeDES];
	//memset((void *) iv, 0x0, (size_t) sizeof(iv));
	Byte iv[] = {0x12, 0x34, 0x56, 0x78, 0x90, 0xAB, 0xCD, 0xEF};
    bufferPtrSize1 = ([sTextIn length] + kCCKeySizeDES) & ~(kCCKeySizeDES -1);
    bufferPtr1 = malloc(bufferPtrSize1 * sizeof(uint8_t));
    memset((void *)bufferPtr1, 0x00, bufferPtrSize1);
	CCCrypt(encryptOrDecrypt, // CCOperation op
			kCCAlgorithmDES, // CCAlgorithm alg
			kCCOptionPKCS7Padding, // CCOptions options
			[dKey bytes], // const void *key
			[dKey length], // size_t keyLength
			iv, // const void *iv
			[dTextIn bytes], // const void *dataIn
			[dTextIn length],  // size_t dataInLength
			(void *)bufferPtr1, // void *dataOut
			bufferPtrSize1,     // size_t dataOutAvailable
			&movedBytes1);      // size_t *dataOutMoved
    
    NSString * sResult;
    if (encryptOrDecrypt == kCCDecrypt)
	{
        sResult = [[ NSString alloc] initWithData:[NSData dataWithBytes:bufferPtr1
																  length:movedBytes1] encoding:EnC];
    }
    else
	{
        NSData *dResult = [NSData dataWithBytes:bufferPtr1 length:movedBytes1];
        sResult = [dResult base64EncodedString];
    }
    free(bufferPtr1);
    return sResult;
}

/*
 DES加密
 */
+ (NSString *)encryptUseDES:(NSString *)clearText key:(NSString *)key
{
    return [self doCipher:clearText key:key context:kCCEncrypt];
}


/**
 DES解密
 */
+ (NSString *)decryptUseDES:(NSString *)plainText key:(NSString *)key
{
    return [self doCipher:plainText key:key context:kCCDecrypt];
}

- (id)objectFromJSONString
{
    NSError *error = nil;
    
    NSData *data = [self dataUsingEncoding:NSUTF8StringEncoding];
    
    if (!data) {
        return nil;
    }
    
    id jsonObject = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
    
    if (!error)
    {
        return jsonObject;
    }
    else
    {
        NSLog(@"The json parse error.");
        
        return nil;
    }
}

- (NSString *)formatJSON
{
    int indentLevel = 0;
    BOOL inString    = NO;
    char currentChar = '\0';
    char *tab = "    ";
    
    NSUInteger len = [self lengthOfBytesUsingEncoding:NSUTF8StringEncoding];
    const char *utf8 = [self UTF8String];
    NSMutableData *buf = [NSMutableData dataWithCapacity:(NSUInteger)(len * 1.1f)];
    
    for (int i = 0; i < len; i++)
    {
        currentChar = utf8[i];
        
        switch (currentChar) {
            case '{':
            case '[':
                if (!inString) {
                    [buf appendBytes:&currentChar length:1];
                    [buf appendBytes:"\n" length:1];
                    
                    for (int j = 0; j < indentLevel+1; j++) {
                        [buf appendBytes:tab length:strlen(tab)];
                    }
                    
                    indentLevel += 1;
                } else {
                    [buf appendBytes:&currentChar length:1];
                }
                break;
            case '}':
            case ']':
                if (!inString) {
                    indentLevel -= 1;
                    [buf appendBytes:"\n" length:1];
                    for (int j = 0; j < indentLevel; j++) {
                        [buf appendBytes:tab length:strlen(tab)];
                    }
                    [buf appendBytes:&currentChar length:1];
                } else {
                    [buf appendBytes:&currentChar length:1];
                }
                break;
            case ',':
                if (!inString) {
                    [buf appendBytes:",\n" length:2];
                    for (int j = 0; j < indentLevel; j++) {
                        [buf appendBytes:tab length:strlen(tab)];
                    }
                } else {
                    [buf appendBytes:&currentChar length:1];
                }
                break;
            case ':':
                if (!inString) {
                    [buf appendBytes:":" length:1];
                } else {
                    [buf appendBytes:&currentChar length:1];
                }
                break;
            case ' ':
            case '\n':
            case '\t':
                if (inString) {
                    [buf appendBytes:&currentChar length:1];
                }
                break;
            case '"':
                
                if (i > 0 && utf8[i-1] != '\\')
                {
                    inString = !inString;
                }
                
                [buf appendBytes:&currentChar length:1];
                break;
            default:
                [buf appendBytes:&currentChar length:1];
                break;
        }
    }
    
    return [[NSString alloc] initWithData:buf encoding:NSUTF8StringEncoding];
}

+ (NSString *)hexStringOfData:(NSData *)data
{
    if (data == nil)
    {
        return nil;
    }
    
    NSMutableString *hexString = [NSMutableString string];
    
    const unsigned char *p = [data bytes];
    
    for (int i=0; i < [data length]; i++)
    {
        [hexString appendFormat:@"%02x", *p++];
    }
    
    return [hexString uppercaseString];
}

+ (NSData *)dataOfHexString:(NSString *)hexString
{
    if (hexString == nil)
    {
        return nil;
    }
    
    const char* ch = [[hexString lowercaseString] cStringUsingEncoding:NSUTF8StringEncoding];
    NSMutableData* data = [NSMutableData data];
    while (*ch)
    {
        char byte = 0;
        if ('0' <= *ch && *ch <= '9')
        {
            byte = *ch - '0';
        }
        else if ('a' <= *ch && *ch <= 'f')
        {
            byte = *ch - 'a' + 10;
        }
        ch++;
        byte = byte << 4;
        if (*ch)
        {
            if ('0' <= *ch && *ch <= '9')
            {
                byte += *ch - '0';
            } else if ('a' <= *ch && *ch <= 'f')
            {
                byte += *ch - 'a' + 10;
            }
            ch++;
        }
        
        [data appendBytes:&byte length:1];
    }
    
    return data;
}

- (NSString *)MD5EncodedString
{
	return [[self dataUsingEncoding:NSUTF8StringEncoding] MD5EncodedString];
}

- (NSData *)HMACSHA1EncodedDataWithKey:(NSString *)key
{
	return [[self dataUsingEncoding:NSUTF8StringEncoding] HMACSHA1EncodedDataWithKey:key];
}

- (NSString *) base64EncodedString
{
	return [[self dataUsingEncoding:NSUTF8StringEncoding] base64EncodedString];
}

+ (NSString *)GUIDString
{
	CFUUIDRef theUUID = CFUUIDCreate(NULL);
	CFStringRef string = CFUUIDCreateString(NULL, theUUID);
	CFRelease(theUUID);
	return (__bridge_transfer NSString *)string;
}
@end
