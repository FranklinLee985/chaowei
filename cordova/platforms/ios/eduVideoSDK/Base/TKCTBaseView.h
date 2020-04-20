//
//  TKCTBaseView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TKCTBaseView : UIView

// 初始化 方法
- (id)initWithFrame:(CGRect)frame;

@property (nonatomic, copy) dispatch_block_t dismissBlock;

@property (nonatomic, strong) UIButton * bgButton;// 半透明背板
@property (nonatomic, strong) UIImageView * backImageView;
@property (nonatomic, strong) UIButton    * closeButton;
@property (nonatomic, strong) UIImageView * contentImageView;
@property (nonatomic, strong) UILabel  * titleLabel;
@property (nonatomic, assign) CGFloat    titleH ; //  标题栏高度
@property (nonatomic, copy)   NSString * titleText;// 设置标题

- (void)show:(UIView *)view;

- (void)show;
- (void)hidden;

/**
 touch事件
 */
- (void)touchOutSide;
//关闭事件可供子类重写
- (void)dismissAlert;

@end
