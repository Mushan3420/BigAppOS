//
//  YZQueryComponents.h
//  YZWpClient
//
//  Created by zhoutl on 15/10/20.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (XQueryComponents)
- (NSString *)stringByDecodingURLFormat;
- (NSString *)stringByEncodingURLFormat;
- (NSMutableDictionary *)dictionaryFromQueryComponents;
@end

@interface NSURL (XQueryComponents)
- (NSMutableDictionary *)queryComponents;
@end

@interface NSDictionary (XQueryComponents)
- (NSString *)stringFromQueryComponents;
@end
