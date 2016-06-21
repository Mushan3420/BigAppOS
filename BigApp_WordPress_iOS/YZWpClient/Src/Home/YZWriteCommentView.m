//
//  YZWriteCommentView.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/30.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZWriteCommentView.h"
#import "BuildConfig.h"

#define kCommentViewButtonWidth 49
#define kContentTextViewMargin  20

@implementation YZWriteCommentView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    
    if (self) {
        
        [self addSubview:self.contentText];
        [self addSubview:self.closeBtn];
        [self addSubview:self.sendCommentBtn];

        [self setSubViewContentFrames];
        
        self.backgroundColor  =[UIColor whiteColor];
    }
    
    return self;
}

- (void)setSubViewContentFrames{
    
        self.frame = CGRectMake(0, kSCREEN_HEIGHT - 179, kSCREEN_WIDTH, 179);
    
    _contentText.frame = CGRectMake(kContentTextViewMargin, kContentTextViewMargin, self.frame.size.width-kContentTextViewMargin*2, 130-kContentTextViewMargin*2);
    
    _closeBtn.frame = CGRectMake(0,
                                 CGRectGetMaxY(_contentText.frame)+kContentTextViewMargin,
                                 kCommentViewButtonWidth,
                                 kCommentViewButtonWidth);
    
    _sendCommentBtn.frame = CGRectMake(self.frame.size.width -kCommentViewButtonWidth,
                                       CGRectGetMaxY(_contentText.frame)+kContentTextViewMargin,
                                       kCommentViewButtonWidth,
                                       kCommentViewButtonWidth);
    
    UIView *lineTop = [[UIView alloc] init];
    lineTop.backgroundColor = RGBCOLOR(210, 210, 210);
    lineTop.frame = CGRectMake(0, 0, kSCREEN_WIDTH, 0.5);
    [self addSubview:lineTop];
    
    UIView *lineCenter = [[UIView alloc] init];
    lineCenter.backgroundColor = RGBCOLOR(210, 210, 210);
    lineCenter.frame = CGRectMake(0, CGRectGetMaxY(_contentText.frame) + kContentTextViewMargin, kSCREEN_WIDTH, 0.5);
    [self addSubview:lineCenter];
    
    UIView *lineRight = [[UIView alloc] init];
    lineRight.backgroundColor = RGBCOLOR(210, 210, 210);
    lineRight.frame = CGRectMake(kCommentViewButtonWidth-0.5, 0, 0.5, kCommentViewButtonWidth);
    [_closeBtn addSubview:lineRight];
    
    UIView *lineLeft = [[UIView alloc] init];
    lineLeft.backgroundColor = RGBCOLOR(210, 210, 210);
    lineLeft.frame = CGRectMake(0, 0, 0.5, kCommentViewButtonWidth);
    [_sendCommentBtn addSubview:lineLeft];
    
    
    

}

-(UITextView *)contentText{
    if (!_contentText) {
        _contentText = [[UITextView alloc] initWithFrame:CGRectZero];
        _contentText.backgroundColor=[UIColor whiteColor]; //背景色
        _contentText.textColor = RGBCOLOR(48, 48, 48);
        _contentText.editable = YES;        //是否允许编辑内容，默认为“YES”
//        _contentText.delegate = self;       //设置代理方法的实现类
        _contentText.font=[UIFont fontWithName:@"Arial" size:16.0]; //设置字体名字和字体大小;
        _contentText.returnKeyType = UIReturnKeyNext;//return键的类型
        _contentText.keyboardType = UIKeyboardTypeDefault;//键盘类型
        _contentText.textAlignment = NSTextAlignmentLeft; //文本显示的位置默认为居左
        _contentText.dataDetectorTypes = UIDataDetectorTypeAll; //显示数据类型的连接模式（如电话号码、网址、地址等）
//        _contentText.textColor = [UIColor blackColor];
    }
    
    return _contentText;
}

- (UIButton *)closeBtn{
    if (!_closeBtn) {
        _closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_closeBtn setImage:[UIImage imageNamed:@"close_comment_button"] forState:UIControlStateNormal];
        
    }
    
    return _closeBtn;
}

- (UIButton *)sendCommentBtn{
    if (!_sendCommentBtn) {
        _sendCommentBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_sendCommentBtn setImage:[UIImage imageNamed:@"comment_send_button"] forState:UIControlStateNormal];
       
    }
    
    return _sendCommentBtn;
}

@end
