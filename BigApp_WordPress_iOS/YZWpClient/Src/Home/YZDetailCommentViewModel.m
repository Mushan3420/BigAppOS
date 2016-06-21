//
//  YZDetailCommentViewModel.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/29.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZDetailCommentViewModel.h"
#import "BuildConfig.h"
@implementation YZDetailCommentViewModel
-(void)setModel:(YZADetailCommentModel *)model{
    
    if (_model != model) {
        _model = model;
        
    }
}

-(NSString *)userNameText{
    return  self.model.author.nickname;
}


-(NSString *)detailText{
    return [self flattenHTML:self.model.content];
}


- (NSString *)dataText{
    
    NSArray  *array = [_model.date_gmt componentsSeparatedByString:@"T"];
    NSArray *array2 = [array.firstObject componentsSeparatedByString:@"-"];
    return [NSString stringWithFormat:@"%@-%@",array2[1],array2.lastObject];
}

- (NSInteger)textRealHeight{
    return [self textHeight:self.detailText width:(kSCREEN_WIDTH - CGRectGetMinX(_imageFrame) * 2)];
}

- (NSInteger)detailTextLabelHeight{
    NSInteger detailTextLabelHeight = self.textRealHeight;
    return detailTextLabelHeight;

}

- (NSInteger)cellHeight{
    
    [self setContentsFrames];
    
    return 70 + self.textRealHeight;
}

- (BOOL)isOpenShow{
    
    //真实高度，大于于最大高度（3行）显示
    
    return (self.textRealHeight > kContentMaxHeight);
}


- (void)setContentsFrames{
    
    _imageFrame = CGRectMake(25, kCommmetCellMarginTop, 40, 40);
    
    
    _userNameLabelFrame = CGRectMake(CGRectGetMaxX(_imageFrame)+4, CGRectGetMinY(_imageFrame), 44, 26);
    
    _dataTextLabelFrame = CGRectMake(CGRectGetMinX(_userNameLabelFrame), CGRectGetMaxY(_userNameLabelFrame), 100, 10);
    
    _detailLabelFrame = CGRectMake(CGRectGetMinX(_imageFrame)+ 5,
                                   CGRectGetMaxY(_imageFrame) + 5,
                                   kSCREEN_WIDTH - CGRectGetMinX(_imageFrame) * 2,
                                   self.detailTextLabelHeight);

    
    
}

#pragma mark --  helper

- (NSString *)flattenHTML:(NSString *)html {
    
    NSScanner *theScanner;
    NSString *text = nil;
    
    theScanner = [NSScanner scannerWithString:html];
    
    while ([theScanner isAtEnd] == NO) {
        
        [theScanner scanUpToString:@"<" intoString:NULL] ;
        
        [theScanner scanUpToString:@">" intoString:&text] ;
        
        html = [html stringByReplacingOccurrencesOfString:
                [NSString stringWithFormat:@"%@>", text]
                                               withString:@""];
    }
    return html;
}//过滤html标签



- (NSInteger)textHeight:(NSString *)text width:(NSInteger)width{
    
    UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:15];
    
    CGSize size = CGSizeMake(width, MAXFLOAT);
    
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    
    [paragraphStyle setLineSpacing:1];
    
    NSDictionary * dic = [NSDictionary dictionaryWithObjectsAndKeys:font, NSFontAttributeName,
                          paragraphStyle,NSParagraphStyleAttributeName,nil];
    
    size =[text boundingRectWithSize:size
                             options:NSStringDrawingUsesLineFragmentOrigin
                          attributes:dic
                             context:nil].size;
    return size.height;
}//计算文本高度




@end
