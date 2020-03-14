//
//  TKCTListView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTListView.h"
#import <QuartzCore/QuartzCore.h>
#import "TKCTDocumentListView.h"
#import "TKEduSessionHandle.h"
#import "TKMediaDocModel.h"

#define ThemeKP(args) [@"TKListView." stringByAppendingString:args]

@interface TKCTListView ()<TKCTDocumentListDelegate>

@property (nonatomic, strong) UIButton * coursewareListButton;//课件库按钮
@property (nonatomic, strong) UIButton * mediaListButton;//媒体库按钮

@property (nonatomic, strong) TKCTDocumentListView *documentListView;//课件库
@property (nonatomic, strong) TKCTDocumentListView *mediaListView;//媒体库

@end

@implementation TKCTListView

- (id)initWithFrame:(CGRect)frame andTitle:(NSString *)title from:(NSString *)from
{
    if (self = [super initWithFrame:frame]) {
        
        //标题空间
        self.titleText = TKMTLocalized(@"Title.DocumentList");
        
        CGFloat leftSpace = self.backImageView.width / 7;
        
        self.contentImageView.frame = CGRectMake(leftSpace, self.titleH, self.backImageView.width - leftSpace - 3, self.backImageView.height - self.titleH - 3);
        self.contentImageView.sakura.image(@"TKBaseView.base_bg_corner_4");
        
        CGFloat courseW = leftSpace - 6;
        CGFloat courseH = courseW / 2;
        CGFloat courseY = self.titleH;
        //课件库按钮
        _coursewareListButton = ({
            UIButton *button = [UIButton buttonWithType:(UIButtonTypeCustom)];
            button.frame = CGRectMake(6, courseY, courseW, courseH);
            [self addSubview:button];
            button.selected = YES;
            button.sakura.image(ThemeKP(@"selector_point_default"), UIControlStateNormal);
            button.sakura.image(ThemeKP(@"selector_point_select"),UIControlStateSelected);
            [button setTitle:[NSString stringWithFormat:@" %@", TKMTLocalized(@"Title.DocumentList")] forState:(UIControlStateNormal)];
            [button setTitle:[NSString stringWithFormat:@" %@", TKMTLocalized(@"Title.DocumentList")] forState:(UIControlStateSelected)];
            button.titleLabel.textAlignment = NSTextAlignmentCenter;
            button.titleLabel.font = [UIFont systemFontOfSize: 12.0];
            button.sakura.backgroundImage(ThemeKP(@"selector_bg_default"),UIControlStateNormal);
            button.sakura.backgroundImage(ThemeKP(@"selector_bg_select"),UIControlStateSelected);
            button.sakura.titleColor(ThemeKP(@"coursewareButtonDefaultColor"),UIControlStateNormal);
            button.sakura.titleColor(ThemeKP(@"coursewareButtonSelectedColor"),UIControlStateSelected);
            button.tag = TKFileListTypeDocument+99;
            [button addTarget:self action:@selector(listButtonClick:) forControlEvents:(UIControlEventTouchUpInside)];
            button;
        });
        
        //媒体库按钮
        _mediaListButton = ({
            UIButton *button = [UIButton buttonWithType:(UIButtonTypeCustom)];
            button.frame = CGRectMake(_coursewareListButton.leftX, CGRectGetMaxY(_coursewareListButton.frame)+10, CGRectGetWidth(_coursewareListButton.frame), CGRectGetHeight(_coursewareListButton.frame));
            [self addSubview:button];
            button.sakura.image(ThemeKP(@"selector_point_default"), UIControlStateNormal);
            button.sakura.image(ThemeKP(@"selector_point_select"),UIControlStateSelected);
            [button setTitle:[NSString stringWithFormat:@"%@ ", TKMTLocalized(@"Title.MediaList")] forState:(UIControlStateNormal)];
            [button setTitle:[NSString stringWithFormat:@"%@ ", TKMTLocalized(@"Title.MediaList")] forState:(UIControlStateSelected)];
            button.titleLabel.textAlignment = NSTextAlignmentCenter;
            button.titleLabel.font = [UIFont systemFontOfSize: 12.0];
            button.sakura.backgroundImage(ThemeKP(@"selector_bg_default"),UIControlStateNormal);
            button.sakura.backgroundImage(ThemeKP(@"selector_bg_select"),UIControlStateSelected);
            button.sakura.titleColor(ThemeKP(@"coursewareButtonDefaultColor"),UIControlStateNormal);
            button.sakura.titleColor(ThemeKP(@"coursewareButtonSelectedColor"),UIControlStateSelected);
            button.tag = TKFileListTypeAudioAndVideo+99;
            button.selected = NO;
            [button addTarget:self action:@selector(listButtonClick:) forControlEvents:(UIControlEventTouchUpInside)];
            button;
        });
        
        //设置字体大小
        [self resetFont];
        
        // 媒体库
        _mediaListView = [[TKCTDocumentListView alloc]initWithFrame:CGRectMake(10, 0, CGRectGetWidth(self.contentImageView.frame), CGRectGetHeight(self.frame))];
        _mediaListView.documentDelegate = self;
        _mediaListView.hidden = YES;
        [self addSubview:_mediaListView];
        
        // 文件库
        _documentListView = [[TKCTDocumentListView alloc]initWithFrame:CGRectMake(10, 0, CGRectGetWidth(self.contentImageView.frame), CGRectGetHeight(self.frame))];
        [self addSubview:_documentListView];
        _documentListView.documentDelegate = self;
        
        //默认状态文档列表显示
        [_documentListView show:TKFileListTypeDocument isClassBegin:[TKEduSessionHandle shareInstance].isClassBegin];
        
        [self newUI];
    }
    return self;
}

- (void)newUI
{
    self.userInteractionEnabled = YES;
    [self.backImageView removeFromSuperview];
    [self.contentImageView removeFromSuperview];
    UIView *backView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.width, self.height)];
    backView.userInteractionEnabled = YES;
    backView.backgroundColor = UIColor.clearColor;
    backView.sakura.backgroundColor(ThemeKP(@"courseware_bg_Color"));
    backView.sakura.alpha(ThemeKP(@"courseware_bg_alpha"));
    backView.backgroundColor = [backView.backgroundColor colorWithAlphaComponent:backView.alpha];
    backView.alpha = 1;
    [self addSubview:backView];
    [self sendSubviewToBack:backView];
    
    CGFloat leftSpace = self.backImageView.width / 7;
    CGFloat courseW = leftSpace - 6;
    CGFloat courseH = courseW / 2;
    CGFloat courseY = 10;
    
    UIView *btnBackView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, courseW + 12, self.height)];
    btnBackView.sakura.backgroundColor(ThemeKP(@"courseware_selectView_bg_Color"));
    btnBackView.sakura.alpha(ThemeKP(@"courseware_selectView_bg_alpha"));
    [backView addSubview:btnBackView];
//    [self sendSubviewToBack:btnBackView];
    
    _coursewareListButton.frame = CGRectMake(6, courseY, courseW, courseH);
    _mediaListButton.frame = CGRectMake(_coursewareListButton.leftX, CGRectGetMaxY(_coursewareListButton.frame)+10, CGRectGetWidth(_coursewareListButton.frame), CGRectGetHeight(_coursewareListButton.frame));
    
    
    _mediaListView.frame = CGRectMake(btnBackView.width, 0, self.width - btnBackView.width, self.height);
    _documentListView.frame = _mediaListView.frame;
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        
        _mediaListButton.hidden = YES;
    }
}

- (void)touchOutSide{
    [self dismissAlert];
}
#pragma mark - 课件库切换
- (void)listButtonClick:(UIButton *)sender{
    
    NSInteger type = sender.tag-99;
    switch (type) {
        case TKFileListTypeDocument:
            
            _coursewareListButton.selected = YES;
            _mediaListButton.selected = NO;
            [_mediaListView hide];
            [_documentListView show:TKFileListTypeDocument isClassBegin:[TKEduSessionHandle shareInstance].isClassBegin];
            
            break;
        case TKFileListTypeAudioAndVideo:
            
            _coursewareListButton.selected = NO;
            _mediaListButton.selected = YES;
            
            [_documentListView hide];
            [_mediaListView show:TKFileListTypeAudioAndVideo isClassBegin:[TKEduSessionHandle shareInstance].isClassBegin];
            
            break;
            
        default:
            break;
    }
}

- (void)resetFont{
    
    if (_coursewareListButton.titleLabel.text ) {
        int currentFontSize = _coursewareListButton.frame.size.width/5;
        if (currentFontSize>14) {
            currentFontSize = 14;
        }
        _coursewareListButton.titleLabel.font = TKFont(currentFontSize);
        _mediaListButton.titleLabel.font = TKFont(currentFontSize);
    }
}

- (void)watchFile{
    [self dismissAlert];
}

- (void)deleteFile {
    [self dismissAlert];
}

- (void)show:(UIView *)view
{
    [view addSubview:self];
    CGRect rect = self.frame;
    self.frame = CGRectMake(ScreenW, rect.origin.y, rect.size.width, rect.size.height);
    [view addSubview:self];
    [view bringSubviewToFront:self];
    
    [UIView animateWithDuration:0.3f animations:^{
        self.frame = CGRectMake(ScreenW - rect.size.width, rect.origin.y, rect.size.width, rect.size.height);
    }];
}

- (void)hidden{
    
    [self dismissAlert];
}

- (void)dismissAlert
{
    [UIView animateWithDuration:0.3f
                     animations:^{
                         
                         CGRect rect = self.frame;
                         self.frame = CGRectMake(ScreenW, rect.origin.y, rect.size.width, rect.size.height);
                         
                     }
                     completion:^(BOOL finished){
                         
                         [self removeFromSuperview];
                         if (self.dismissBlock) {
                             self.dismissBlock();
                         }
                     }];
    
    
}
- (UIViewController *)appRootViewController
{
    UIViewController *appRootVC = [UIApplication sharedApplication].keyWindow.rootViewController;
    UIViewController *topVC = appRootVC;
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}
- (void)removeFromSuperview
{
    [super removeFromSuperview];
}
@end


