//
//  YZHistory.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/30.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZHistory.h"

@implementation YZHistory

@synthesize ID,historyTitle,historyDate;

- (id)init
{
    self = [super init];
    if (self) {
        NSDateFormatter *sdf = [[NSDateFormatter alloc]init];
        [sdf setDateFormat:@"yyyy年MM月dd日"];
        self.historyDate = [sdf stringFromDate:[NSDate date]];
    }
    return self;
}

@end
