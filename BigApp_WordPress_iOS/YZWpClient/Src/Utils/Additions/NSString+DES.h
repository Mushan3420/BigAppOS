//
//  NSString+DES.h
//  SuningEBuy
//
//  Created by  liukun on 12-11-15.
//  Copyright (c) 2012年 Suning. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NSData+Base64.h"
@interface NSString (DES)

+(NSString *) encryptUseDES:(NSString *)clearText key:(NSString *)key;

+(NSString *) decryptUseDES:(NSString *)plainText key:(NSString *)key;


@end
