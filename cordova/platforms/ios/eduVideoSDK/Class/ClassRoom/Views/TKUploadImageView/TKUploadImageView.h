//
//  TKUploadImageView.h
//  EduClassPad
//
//  Created by ifeng on 2017/10/17.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TKUploadImageView : UIImageView
@property (nonatomic, assign) SEL action;
@property (nonatomic, assign) id target;

@property (nonatomic , strong) UILabel * progressLabel;
@property (nonatomic , strong) UIView * progressView;
@property (nonatomic , strong) UIButton * cancelButton;
- (void)setProgress:(CGFloat)progress;
@end
