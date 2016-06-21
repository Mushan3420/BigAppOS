//
//  NSStringAdditions.m
//  Weibo
//
//  Created by junmin liu on 10-9-29.
//  Copyright 2010 Openlab. All rights reserved.
//

#import "NSString+Additions.h"

@implementation NSString (Additions)

+ (NSString *)generateGuid {
	CFUUIDRef	uuidObj = CFUUIDCreate(nil);//create a new UUID
	//get the string representation of the UUID
	NSString	*uuidString = (NSString*)CFBridgingRelease(CFUUIDCreateString(nil, uuidObj));
	CFRelease(uuidObj);
	return uuidString;
}

///////////////////////////////////////////////////////////////////////////////////////////////////
- (BOOL)isWhitespaceAndNewlines {
	NSCharacterSet* whitespace = [NSCharacterSet whitespaceAndNewlineCharacterSet];
	for (NSInteger i = 0; i < self.length; ++i) {
		unichar c = [self characterAtIndex:i];
		if (![whitespace characterIsMember:c]) {
			return NO;
		}
	}
	return YES;
}


///////////////////////////////////////////////////////////////////////////////////////////////////
- (BOOL)isEmptyOrWhitespace {
	return !self.length ||
	![self stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]].length;
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Copied and pasted from http://www.mail-archive.com/cocoa-dev@lists.apple.com/msg28175.html
- (NSDictionary*)queryDictionaryUsingEncoding:(NSStringEncoding)encoding {
	NSCharacterSet* delimiterSet = [NSCharacterSet characterSetWithCharactersInString:@"&;"];
	NSMutableDictionary* pairs = [NSMutableDictionary dictionary];
	NSScanner* scanner = [[NSScanner alloc] initWithString:self];
	while (![scanner isAtEnd]) {
		NSString* pairString = nil;
		[scanner scanUpToCharactersFromSet:delimiterSet intoString:&pairString];
		[scanner scanCharactersFromSet:delimiterSet intoString:NULL];
		NSArray* kvPair = [pairString componentsSeparatedByString:@"="];
		if (kvPair.count == 2) {
			NSString* key = [[kvPair objectAtIndex:0]
							 stringByReplacingPercentEscapesUsingEncoding:encoding];
			NSString* value = [[kvPair objectAtIndex:1]
							   stringByReplacingPercentEscapesUsingEncoding:encoding];
			[pairs setObject:value forKey:key];
		}
	}
	
	return [NSDictionary dictionaryWithDictionary:pairs];
}


///////////////////////////////////////////////////////////////////////////////////////////////////
- (NSString*)stringByAddingQueryDictionary:(NSDictionary*)query {
	NSMutableArray* pairs = [NSMutableArray array];
	for (NSString* key in [query keyEnumerator]) {
		NSString* value = [query objectForKey:key];
		value = [value stringByReplacingOccurrencesOfString:@"?" withString:@"%3F"];
		value = [value stringByReplacingOccurrencesOfString:@"=" withString:@"%3D"];
		NSString* pair = [NSString stringWithFormat:@"%@=%@", key, value];
		[pairs addObject:pair];
	}
	
	NSString* params = [pairs componentsJoinedByString:@"&"];
	if ([self rangeOfString:@"?"].location == NSNotFound) {
		return [self stringByAppendingFormat:@"?%@", params];
	} else {
		return [self stringByAppendingFormat:@"&%@", params];
	}
}


///////////////////////////////////////////////////////////////////////////////////////////////////
- (NSComparisonResult)versionStringCompare:(NSString *)other {
	NSArray *oneComponents = [self componentsSeparatedByString:@"a"];
	NSArray *twoComponents = [other componentsSeparatedByString:@"a"];
	
	// The parts before the "a"
	NSString *oneMain = [oneComponents objectAtIndex:0];
	NSString *twoMain = [twoComponents objectAtIndex:0];
	
	// If main parts are different, return that result, regardless of alpha part
	NSComparisonResult mainDiff;
	if ((mainDiff = [oneMain compare:twoMain]) != NSOrderedSame) {
		return mainDiff;
	}
	
	// At this point the main parts are the same; just deal with alpha stuff
	// If one has an alpha part and the other doesn't, the one without is newer
	if ([oneComponents count] < [twoComponents count]) {
		return NSOrderedDescending;
	} else if ([oneComponents count] > [twoComponents count]) {
		return NSOrderedAscending;
	} else if ([oneComponents count] == 1) {
		// Neither has an alpha part, and we know the main parts are the same
		return NSOrderedSame;
	}
	
	// At this point the main parts are the same and both have alpha parts. Compare the alpha parts
	// numerically. If it's not a valid number (including empty string) it's treated as zero.
	NSNumber *oneAlpha = [NSNumber numberWithInt:[[oneComponents objectAtIndex:1] intValue]];
	NSNumber *twoAlpha = [NSNumber numberWithInt:[[twoComponents objectAtIndex:1] intValue]];
	return [oneAlpha compare:twoAlpha];
}


///////////////////////////////////////////////////////////////////////////////////////////////////
- (NSString*)md5Hash 
{
	return [NSString md5HexDigest:self];
}


- (CGSize)heightWithFont:(UIFont*)withFont 
                   width:(float)width 
               linebreak:(UILineBreakMode)lineBreakMode{
	
	
//	[withFont retain];
	CGSize suggestedSize = [self sizeWithFont:withFont constrainedToSize:CGSizeMake(width, FLT_MAX) lineBreakMode:lineBreakMode];
	
	return suggestedSize;
}

- (NSString *)replacedWhiteSpacsByString:(NSString *)replaceString{
    
    if (replaceString == nil || [replaceString isEqual:[NSNull null]] ) {
        return nil;
    }
    
    NSString *memberNameRegex = @"\\s+";
    
    NSString *replace = [self stringByReplacingOccurrencesOfString:memberNameRegex withString:replaceString options:NSRegularExpressionSearch range:NSMakeRange(0, [self length])];
    
    return replace;
    
}

- (NSString *)trim
{
    NSString *trimStr = [self stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    return trimStr;
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

- (BOOL)eq:(NSString *)str
{
    return [self isEqualToString:str];
}

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
        NSString * value = nil;
		id val = [dict objectForKey:key];
        if ([val isKindOfClass:[NSNumber class]]) {
            value = [val stringValue];
        }else if ([val isKindOfClass:[NSString class]]){
            value = val;
        }else{
            continue;
        }
        
		NSString * urlEncoding = [value URLEncoding];
		[pairs addObject:[NSString stringWithFormat:@"%@=%@", key, urlEncoding]];
	}
	
	return [pairs componentsJoinedByString:@"&"];
}

- (NSString *)queryStringNoEncodeFromDictionary:(NSDictionary *)dict
{
    NSMutableArray * pairs = [NSMutableArray array];
	for ( NSString * key in [dict keyEnumerator] )
	{
        NSString * value = nil;
		id val = [dict objectForKey:key];
        if ([val isKindOfClass:[NSNumber class]]) {
            value = [val stringValue];
        }else if ([val isKindOfClass:[NSString class]]){
            value = val;
        }else{
            continue;
        }
        
		[pairs addObject:[NSString stringWithFormat:@"%@=%@", key, value]];
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
@end
