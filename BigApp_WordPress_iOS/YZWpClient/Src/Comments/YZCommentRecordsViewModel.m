//
//  YZDetailCommentViewModel.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/29.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZCommentRecordsViewModel.h"
#import "BuildConfig.h"
@implementation YZCommentRecordsViewModel
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
    return nil;
}

- (NSInteger)textRealHeight{
    return [self textHeight:self.detailText width:(kSCREEN_WIDTH - 40 - 28)];
}

- (NSInteger)detailTextLabelHeight{
    NSInteger detailTextLabelHeight = self.textRealHeight;
    
    if (self.isRealHeight) {
        return detailTextLabelHeight;
    }else{
        if (detailTextLabelHeight >kContentMaxHeight) {
            detailTextLabelHeight = kContentMaxHeight;
        }
    }
    
    return detailTextLabelHeight;

}

- (NSInteger)cellHeight{
    
    [self setContentsFrames];
    
    if (self.detailTextLabelHeight < 45) {
        return 45;
    }
    else{
        if (self.isRealHeight) {
            return CGRectGetMaxY(_detailLabelFrame);
        }else{
            return 80;
        }

    }
}

- (BOOL)isOpenShow{
    
    //真实高度，大于于最大高度（3行）显示
    
    return (self.textRealHeight > kContentMaxHeight);
}


- (void)setContentsFrames{
    
    
    if (self.detailTextLabelHeight < 45) {
        _detailLabelFrame = CGRectMake(20,
                                       13.5,
                                      kSCREEN_WIDTH - 40 - 28,
                                       self.detailTextLabelHeight);
    }
    else{
        _detailLabelFrame = CGRectMake(20,
                                       kCommmetCellMarginTop-1,
                                       kSCREEN_WIDTH - 40 - 28,
                                       self.detailTextLabelHeight);
    }

    _postViewFrame = CGRectMake(CGRectGetMinX(_detailLabelFrame),
                                CGRectGetMaxY(_detailLabelFrame)+10,
                                kSCREEN_WIDTH - 40,
                                34);
    
    
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
    
    [paragraphStyle setLineSpacing:0];
    
    NSDictionary * dic = [NSDictionary dictionaryWithObjectsAndKeys:font, NSFontAttributeName,
                          paragraphStyle,NSParagraphStyleAttributeName,nil];
    
    size =[text boundingRectWithSize:size
                             options:NSStringDrawingUsesLineFragmentOrigin
                          attributes:dic
                             context:nil].size;
    return size.height;
}//计算文本高度




@end
