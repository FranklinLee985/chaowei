//
//  TKVideoPopupMenu.m
//  EduClass
//
//  Created by lyy on 2018/4/23.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKVideoPopupMenu.h"
#import "TKPopupMenuPath.h"
#import "TKVideoFunctionView.h"
#import "TKVideoVerticalFunctionView.h"
#import "TKEduSessionHandle.h"
#import <TKRoomSDK/TKRoomSDK.h>
#define ThemeKP(args) [@"ClassRoom.TKVideoView." stringByAppendingString:args]

#define CollectionCellHeight 40
@interface TKVideoPopupMenu ()<VideolistProtocol,VideoVlistProtocol>


/**
 显示控制按钮视图
 */
@property (nonatomic, strong) UIView * videoToolView;

@property (nonatomic) CGRect                relyRect;
@property (nonatomic, assign) CGFloat       itemWidth;
@property (nonatomic) CGPoint               point;
@property (nonatomic, assign) BOOL          isCornerChanged;
@property (nonatomic, strong) UIColor     * separatorColor;
@property (nonatomic, assign) BOOL          isChangeDirection;
@property (nonatomic, assign) int	        controlButtonCount;
@end

@implementation TKVideoPopupMenu

- (instancetype)init
{
    self = [super init];
    if (self) {
        [self setDefaultSettings];
        
    }
    return self;
}

#pragma mark - publics

+ (TKVideoPopupMenu *)showRelyOnView:(UIView *)view
                            aRoomUer:(TKRoomUser*)roomUser
                             isSplit:(BOOL)isSplit
                           isSpeaker:(BOOL)isSpeaker
                            delegate:(id<TKVideoPopupMenuDelegate>)delegate;
{
    //转换view层级统一到window
    CGRect viewFrame = [view.superview convertRect:view.bounds toView:TKMainWindow];
    //relyPoint为view的center在window上的坐标
    CGPoint point = CGPointMake(viewFrame.origin.x + viewFrame.size.width / 2, viewFrame.origin.y + viewFrame.size.height);
    
    TKVideoPopupMenu *popupMenu = [[TKVideoPopupMenu alloc] init];
    popupMenu.point = point;        //依赖view的center在window上的位置
    popupMenu.relyRect = viewFrame;  //依赖view在window上的frame
    popupMenu.delegate = delegate;
    popupMenu.iRoomUser = roomUser;
    
    popupMenu.isSplit = isSplit;
    popupMenu.isSpeaker = isSpeaker;

    //判断显示控制按钮的个数（根据角色、拖拽状态、分屏状态）
    popupMenu.controlButtonCount = [popupMenu returnControlButtonCountRoomUser:roomUser isSplit:isSplit];

    if ([TKEduSessionHandle shareInstance].roomLayout == oneToOneDoubleVideo) {
        popupMenu.arrowDirection = TKPopupMenuArrowDirectionCenter;
        popupMenu.priorityDirection = TKPopupMenuPriorityDirectionNone;
        popupMenu.itemWidth =popupMenu.controlButtonCount * 60;
    }
    else if (([TKEduClassRoom shareInstance].roomJson.roomtype == TKRoomTypeOneToOne &&
         ![TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish)  ) {
        //纯一对一教室
        popupMenu.arrowDirection = TKPopupMenuArrowDirectionRight;
        popupMenu.priorityDirection = TKPopupMenuPriorityDirectionRight;
        
        popupMenu.itemWidth = CollectionCellHeight;
        popupMenu.itemHeight= popupMenu.controlButtonCount * CollectionCellHeight;
        
    }else if(isSplit){
        
        popupMenu.arrowDirection = TKPopupMenuArrowDirectionBottom;
        popupMenu.priorityDirection = TKPopupMenuPriorityDirectionBottom;
        
        popupMenu.itemWidth =popupMenu.controlButtonCount * 60;
        
    } else{
        
        popupMenu.arrowDirection = TKPopupMenuArrowDirectionTop;
        popupMenu.priorityDirection = TKPopupMenuPriorityDirectionTop;
        popupMenu.itemWidth =popupMenu.controlButtonCount * 60;
        
        // 位置调整
        if (popupMenu.y < 0 || (popupMenu.y + popupMenu.height > ScreenH)) {
            
            popupMenu.centerY = view.centerY;

        }
    }
    
    if (isSpeaker) {
        popupMenu.arrowDirection = TKPopupMenuArrowDirectionTop;
        popupMenu.priorityDirection = TKPopupMenuPriorityDirectionTop;
        popupMenu.itemWidth =popupMenu.controlButtonCount * 60;

    }
    
    [popupMenu show];
    [popupMenu resetVideoToolFrame:CGRectMake(0, 0, popupMenu.width,popupMenu.height)];
    
    // 双师偏移出 隐藏视频按钮位置
    if ([TKEduSessionHandle shareInstance].roomLayout == oneToOneDoubleDivision)
    {
        if (view.bottomY < (ScreenH / 2)) {
            popupMenu.x = popupMenu.x - 25;
        }
    }
    
    return popupMenu;
}

-(UIView *)videoToolView{
    
    if (!_videoToolView) {
        
        // 一对一 助教不允许上台 飞双视频模式 纵向弹窗
        if (([TKEduClassRoom shareInstance].roomJson.roomtype == TKRoomTypeOneToOne &&
             ![TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish &&
             [TKEduSessionHandle shareInstance].roomLayout != oneToOneDoubleVideo)) {
            
            
            _videoToolView = [[TKVideoVerticalFunctionView  alloc] initWithFrame:CGRectMake(0, 0, CollectionCellHeight + 10, CollectionCellHeight)  aRoomUer:_iRoomUser isSplit:_isSplit count:_controlButtonCount];
            
            TKVideoVerticalFunctionView *view = (TKVideoVerticalFunctionView *)_videoToolView;
            
            view.iDelegate = self;
            
        }else{// 一对多 横向弹窗
            
            _videoToolView = [[TKVideoFunctionView  alloc] initWithFrame:CGRectMake(0, 0, _itemWidth, 69) aRoomUer:_iRoomUser isSplit:_isSplit isSpeaker:_isSpeaker count:_controlButtonCount];
            TKVideoFunctionView *view = (TKVideoFunctionView *)_videoToolView;
            
            view.iDelegate = self;
        }
    }
    return _videoToolView;
}

- (int)returnControlButtonCountRoomUser:(TKRoomUser *)aRoomUer isSplit:(BOOL)isSplit{
    
    //默认按钮个数为0
    int btnNum = 0;
    
    // 演讲按钮暂时去掉
    btnNum = (aRoomUer.disableVideo || [TKEduSessionHandle shareInstance].isOnlyAudioRoom) ? 0 : 1;
    btnNum += aRoomUer.disableAudio ? 0 : 1;

    // 操作人角色
    TKUserRoleType localRole = [[TKEduSessionHandle shareInstance] localUser].role;
    
    // 上台 授权 奖杯
    if (aRoomUer.role == TKUserType_Teacher) {
        
        if (localRole == TKUserType_Teacher) {
            // 老师点击自己 只有音视频
            if ([TKEduClassRoom shareInstance].roomJson.roomtype != 0 || [TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish) {
                
                btnNum += (self.isSplit ? 2 : 1);//恢复位置 全体复位
            }

        } else if (localRole == TKUserType_Student) {
            // 学生点击老师 不允许
        } else {
            btnNum = 0;
        }
        
    }
    else if (aRoomUer.role == TKUserType_Student) {
        
        if (localRole == TKUserType_Teacher) {
            // 老师点击学生 允许助教上台的一对一 会进入一对多房间
            if ([TKEduClassRoom shareInstance].roomJson.roomtype == TKRoomTypeOneToOne &&
                [TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish == NO) {
                btnNum += 2;// 授权 奖杯


            } else {
                btnNum += 3;// 授权 奖杯 上台
            }
        
            if (self.isSplit) {
                btnNum += 1;// 恢复位置
            }
            
        } else if (localRole == TKUserType_Student) {
            // 学生点击自己 只有音视频
        } else {
            btnNum = 0;
        }
        
    }
    else if (aRoomUer.role == TKUserType_Assistant) {
        
        if (localRole == TKUserType_Teacher) {
            // 老师点击助教
            btnNum += 1;// 上台
            
            if (self.isSplit) {
                btnNum += 1;// 恢复位置
            }

        } else if (localRole == TKUserType_Student) {
            // 学生点击助教 不允许
        } else {
            btnNum = 0;
        }
        
    }
    else {
        btnNum = 0;
    }
    
    // 一对一双师 一对多主讲加入切换
    if ([TKEduSessionHandle shareInstance].roomLayout == MainPeople ||
        [TKEduSessionHandle shareInstance].roomLayout == oneToOneDoubleDivision) {
        
        if (localRole == TKUserType_Teacher) {
            if (!self.isSpeaker)
            {
                btnNum ++;
            }
        } else if (localRole == TKUserType_Student && aRoomUer.role == TKUserType_Student) {
            
            btnNum ++;
        }
    }
    
    return btnNum;
}

- (void)dismiss
{
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
    [self performSelector:@selector(modifierFunction) withObject:nil afterDelay:0.3];
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(ybPopupMenuBeganDismiss)]) {
        [self.delegate ybPopupMenuBeganDismiss];
    }
    [UIView animateWithDuration: 0.25 animations:^{
        self.layer.affineTransform = CGAffineTransformMakeScale(0.1, 0.1);
        self.alpha = 0;
    } completion:^(BOOL finished) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(ybPopupMenuDidDismiss)]) {
            [self.delegate ybPopupMenuDidDismiss];
        }
        self.delegate = nil;
        [self removeFromSuperview];
    }];
}

- (void)modifierFunction
{
    [TKEduSessionHandle shareInstance].dismissing = NO;
}


#pragma mark - privates
- (void)show
{    
    [self addSubview:self.videoToolView];
    
    [TKMainWindow addSubview:self];


    if (self.delegate && [self.delegate respondsToSelector:@selector(ybPopupMenuBeganShow)]) {
        [self.delegate ybPopupMenuBeganShow];
    }
    self.layer.affineTransform = CGAffineTransformMakeScale(0.1, 0.1);
    [UIView animateWithDuration: 0.25 animations:^{
        self.layer.affineTransform = CGAffineTransformMakeScale(1.0, 1.0);
        self.alpha = 1;
    } completion:^(BOOL finished) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(ybPopupMenuDidShow)]) {
            [self.delegate ybPopupMenuDidShow];
        }
    }];
}

- (void)showonView {
    
    UIViewController *topVC = [self appRootViewController];
    
    [topVC.view addSubview:self];
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

- (void)setDefaultSettings
{
    _cornerRadius = 10.0;
    _rectCorner = UIRectCornerAllCorners;
    self.isShowShadow = NO;//设置阴影
    _dismissOnSelected = YES;
    _dismissOnTouchOutside = YES;
    _offset = 0.0;
    _relyRect = CGRectZero;
    _point = CGPointZero;
    _borderWidth = [TKTheme floatWithPath:ThemeKP(@"videoPopBorderWidth")]; //设置边框宽度
    _borderColor = [[TKTheme colorWithPath:ThemeKP(@"videoPopBorderColor")] colorWithAlphaComponent:[TKTheme floatWithPath:ThemeKP(@"videoPopAlpha")]];//设置边框颜色

    _arrowWidth = 15.0;
    _arrowHeight = 10.0;
    _backColor = [[TKTheme colorWithPath:ThemeKP(@"videoPopBackColor")] colorWithAlphaComponent:[TKTheme floatWithPath:ThemeKP(@"videoPopAlpha")]];//设置背景颜色
    
    _minSpace = 10.0;
    _itemHeight = 69;
    _isCornerChanged = NO;
    _showMaskView = YES;
    self.alpha = 0;
    self.backgroundColor = [UIColor clearColor];
    
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    //不在self内 并且不在smallview内
    if (![self pointInside:point withEvent:event]) {

        [self touchOutSide];
        [TKEduSessionHandle shareInstance].dismissing = YES;
        return nil;
    }
    return [super hitTest:point withEvent:event];
}

- (void)touchOutSide
{
    [self dismiss];
}

#pragma mark - TKVideoFunctionView Delegate 实现
-(void)videoSmallbutton1:(UIButton *)aButton {
    //根据角色判断  老师：关闭视频  学生：授权涂鸦
    if (self.delegate && [self.delegate respondsToSelector:@selector(videoPopupMenuCanDraw:)] ) {
        [self.delegate videoPopupMenuCanDraw:aButton];
    }
}
-(void)videoSmallButton2:(UIButton *)aButton {
    if (self.delegate && [self.delegate respondsToSelector:@selector(videoPopupMenuUnderPlatform:)] ) {
        [self.delegate videoPopupMenuUnderPlatform:aButton];
    }
}
-(void)videoSmallButton3:(UIButton *)aButton {
    if (self.delegate && [self.delegate respondsToSelector:@selector(videoPopupMenuControlAudio:)] ) {
        [self.delegate videoPopupMenuControlAudio:aButton];
    }
}
-(void)videoSmallButton4:(UIButton *)aButton {
    if (self.delegate && [self.delegate respondsToSelector:@selector(videoPopupMenuSendGif:)] ) {
        [self.delegate videoPopupMenuSendGif:aButton];
    }
}
-(void)videoSmallButton5:(UIButton *)aButton {
    if (self.delegate && [self.delegate respondsToSelector:@selector(videoPopupMenuControlVideo:)] ) {
        [self.delegate videoPopupMenuControlVideo:aButton];
    }
}
-(void)videoSmallButton6:(UIButton *)aButton {
    if (self.delegate && [self.delegate respondsToSelector:@selector(videoSplitScreenVideo:)] ) {
        [self.delegate videoSplitScreenVideo:aButton];
    }
}
-(void)videoSmallButton7:(UIButton *)aButton {
    if (self.delegate && [self.delegate respondsToSelector:@selector(videoPopupMenuControlRestorePosition:)]) {
        [self.delegate videoPopupMenuControlRestorePosition:aButton];
    }
}
-(void)videoSmallButton8:(UIButton *)aButton {
    if (self.delegate && [self.delegate respondsToSelector:@selector(videoPopupMenuControlRestoreAll:)]) {
        [self.delegate videoPopupMenuControlRestoreAll:aButton];
    }
}

-(void)didPressChangeButton
{
    if ([self.delegate respondsToSelector:@selector(didPressChangeButton)]) {
        [self.delegate didPressChangeButton];
    }
}

#pragma mark - 初始化设置 以及 位置设置
- (void)updateUI
{
    CGFloat height;
    height = _itemHeight;
    
    _isChangeDirection = NO;
    if (_priorityDirection == TKPopupMenuPriorityDirectionTop) {
        if (_point.y + height + _arrowHeight > ScreenH - _minSpace) {
            _arrowDirection = TKPopupMenuArrowDirectionBottom;
            _isChangeDirection = YES;
        }else {
            _arrowDirection = TKPopupMenuArrowDirectionTop;
            _isChangeDirection = NO;
        }
    }else if (_priorityDirection == TKPopupMenuPriorityDirectionBottom) {
        
        if ((_point.y - height - _arrowHeight < _minSpace) && !_isSplit) {
            _arrowDirection = TKPopupMenuArrowDirectionTop;
            _isChangeDirection = YES;
        }else {
            _arrowDirection = TKPopupMenuArrowDirectionBottom;
            _isChangeDirection = NO;
        }
    }else if (_priorityDirection == TKPopupMenuPriorityDirectionLeft) {
        if (_point.x + _itemWidth + _arrowHeight > ScreenW - _minSpace) {
            _arrowDirection = TKPopupMenuArrowDirectionRight;
            _isChangeDirection = YES;
        }else {
            _arrowDirection = TKPopupMenuArrowDirectionLeft;
            _isChangeDirection = NO;
        }
    }else if (_priorityDirection == TKPopupMenuPriorityDirectionRight) {
        //60        //10            //10
        if (_point.x - _itemWidth - _arrowHeight < _minSpace) {
            _arrowDirection = TKPopupMenuArrowDirectionLeft;
            _isChangeDirection = YES;
        }else {
            _arrowDirection = TKPopupMenuArrowDirectionRight;
            _isChangeDirection = NO;
        }
    }
    [self setArrowPosition];
    [self setRelyRect];
    if (_arrowDirection == TKPopupMenuArrowDirectionTop) {
        CGFloat y = _isChangeDirection ? _point.y  : _point.y;
        if (_arrowPosition > _itemWidth / 2) {
            self.frame = CGRectMake(ScreenW - _minSpace - _itemWidth, y, _itemWidth, height + _arrowHeight);
        }else if (_arrowPosition < _itemWidth / 2) {
            self.frame = CGRectMake(_minSpace, y, _itemWidth, height + _arrowHeight);
        }else {
            self.frame = CGRectMake(_point.x - _itemWidth / 2, y, _itemWidth, height + _arrowHeight);
        }
        
        if (self.isSpeaker) {
            self.frame = CGRectMake(CGRectGetMidX(_relyRect) - _itemWidth/2, CGRectGetMidY(_relyRect), _itemWidth, height + _arrowHeight);
        }
    }else if (_arrowDirection == TKPopupMenuArrowDirectionBottom) {
        
        if (_isSplit) {//分屏状态下
            
            CGFloat x = CGRectGetMidX(_relyRect) - _itemWidth/2 <= 0 ? 0 : CGRectGetMidX(_relyRect) - _itemWidth/2;
            x = x + _itemWidth >= ScreenW ? ScreenW - _itemWidth : x;
            
            self.frame = CGRectMake(x,CGRectGetMinY(_relyRect)- height - _arrowHeight, _itemWidth,  height + _arrowHeight);
        
        

        }else{
            CGFloat y = _isChangeDirection ? _point.y - _arrowHeight - height : _point.y - _arrowHeight - height;
            if (_arrowPosition > _itemWidth / 2) {
                self.frame = CGRectMake(ScreenW - _minSpace - _itemWidth, y, _itemWidth, height + _arrowHeight);
            }else if (_arrowPosition < _itemWidth / 2) {
                self.frame = CGRectMake(_minSpace, y, _itemWidth, height + _arrowHeight);
            }else {
                self.frame = CGRectMake(_point.x - _itemWidth / 2, y, _itemWidth, height + _arrowHeight);
            }
        }
        
        
    }else if (_arrowDirection == TKPopupMenuArrowDirectionLeft) {
        CGFloat x = _isChangeDirection ? _point.x : _point.x;
        if (_arrowPosition < _itemHeight / 2) {
            self.frame = CGRectMake(x, _point.y - _arrowPosition, _itemWidth + _arrowHeight, height);
        }else if (_arrowPosition > _itemHeight / 2) {
            self.frame = CGRectMake(x, _point.y - _arrowPosition, _itemWidth + _arrowHeight, height);
        }else {
            self.frame = CGRectMake(x, _point.y - _arrowPosition, _itemWidth + _arrowHeight, height);
        }
    }else if (_arrowDirection == TKPopupMenuArrowDirectionRight) { //箭头在右边   主要在1v1模式下使用 || 经典原结构的老师视频下
        
        CGFloat x = _point.x - _itemWidth - _arrowHeight;
        CGFloat y = _point.y - _arrowPosition;
        if ((_iRoomUser.role != TKUserType_Teacher) && IS_IPHONE && _controlButtonCount>3) {
            y = y - _relyRect.size.height;
            if (y<0) {
                y=0;
            }
        }                   //弹窗总高度
        if (_arrowPosition < _itemHeight / 2) {
            self.frame = CGRectMake(x, y, _itemWidth + _arrowHeight, height);
        }else if (_arrowPosition > _itemHeight / 2) {
            self.frame = CGRectMake(x, y, _itemWidth + _arrowHeight, height);
        }else {
            self.frame = CGRectMake(x, y, _itemWidth + _arrowHeight, height);
        }
        
        if (CGRectGetHeight(self.frame) >= CGRectGetHeight(_relyRect)) {
            if (_relyRect.origin.y + height >= ScreenH) {
                self.frame = CGRectMake(x, CGRectGetMaxY(_relyRect) - height, _itemWidth + _arrowHeight, height);
            } else {
                self.frame = CGRectMake(x, _relyRect.origin.y, _itemWidth + _arrowHeight, height);
            }
        } else {
            self.center = CGPointMake(self.center.x, _relyRect.origin.y + CGRectGetHeight(_relyRect) / 2 + height / 2);
        }
        
        //重置一下箭头指向依赖view的中心
        _arrowPosition = CGRectGetMinY(_relyRect) + CGRectGetHeight(_relyRect) / 2 - CGRectGetMinY(self.frame);
        
    }else if (_arrowDirection == TKPopupMenuArrowDirectionNone) {
        
    }
    
    if (_isChangeDirection) {
        [self changeRectCorner];
    }
    [self setAnchorPoint];
    [self setOffset];
    [self setNeedsDisplay];
}
- (void)setIsShowShadow:(BOOL)isShowShadow
{
    _isShowShadow = isShowShadow;
    self.layer.shadowOpacity = isShowShadow ? 0.5 : 0;
    self.layer.shadowOffset = CGSizeMake(0, 0);
    self.layer.shadowRadius = isShowShadow ? 2.0 : 0;
}

- (void)setShowMaskView:(BOOL)showMaskView
{
    _showMaskView = showMaskView;
}

- (void)setPoint:(CGPoint)point
{
    _point = point;
    [self updateUI];
}

- (void)setItemWidth:(CGFloat)itemWidth
{
    _itemWidth = itemWidth;
    [self updateUI];
}

- (void)setItemHeight:(CGFloat)itemHeight
{
    _itemHeight = itemHeight;
    [self updateUI];
}

- (void)setBorderWidth:(CGFloat)borderWidth
{
    _borderWidth = borderWidth;
    [self updateUI];
}

- (void)setBorderColor:(UIColor *)borderColor
{
    _borderColor = borderColor;
    [self updateUI];
}

- (void)setArrowPosition:(CGFloat)arrowPosition
{
    _arrowPosition = arrowPosition;
    [self updateUI];
}

- (void)setArrowWidth:(CGFloat)arrowWidth
{
    _arrowWidth = arrowWidth;
    [self updateUI];
}

- (void)setArrowHeight:(CGFloat)arrowHeight
{
    _arrowHeight = arrowHeight;
    [self updateUI];
}

- (void)setArrowDirection:(YBPopupMenuArrowDirection)arrowDirection
{
    _arrowDirection = arrowDirection;
    [self updateUI];
}

- (void)setMaxVisibleCount:(NSInteger)maxVisibleCount
{
    [self updateUI];
}

- (void)setBackColor:(UIColor *)backColor
{
    _backColor = backColor;
    [self updateUI];
}

- (void)setPriorityDirection:(YBPopupMenuPriorityDirection)priorityDirection
{
    _priorityDirection = priorityDirection;
    [self updateUI];
}

- (void)setRectCorner:(UIRectCorner)rectCorner
{
    _rectCorner = rectCorner;
    [self updateUI];
}

- (void)setCornerRadius:(CGFloat)cornerRadius
{
    _cornerRadius = cornerRadius;
    [self updateUI];
}

- (void)setOffset:(CGFloat)offset
{
    _offset = offset;
    [self updateUI];
}

- (void)setRelyRect
{
    if (CGRectEqualToRect(_relyRect, CGRectZero)) {
        return;
    }
    if (_arrowDirection == TKPopupMenuArrowDirectionTop) {
        _point.y = _relyRect.size.height + _relyRect.origin.y;
    }else if (_arrowDirection == TKPopupMenuArrowDirectionBottom) {
        _point.y = _relyRect.origin.y;
    }else if (_arrowDirection == TKPopupMenuArrowDirectionLeft) {
        _point = CGPointMake(_relyRect.origin.x + _relyRect.size.width, _relyRect.origin.y + _relyRect.size.height / 2);
    }else {
        _point = CGPointMake(_relyRect.origin.x, _relyRect.origin.y);   //point设置为视频原点
    }
}

//设置内容区域
- (void)resetVideoToolFrame:(CGRect)frame
{
    if (_arrowDirection == TKPopupMenuArrowDirectionTop) {
        self.videoToolView.frame = CGRectMake(_borderWidth, _borderWidth + _arrowHeight, frame.size.width - _borderWidth * 2, frame.size.height - _arrowHeight);
    }else if (_arrowDirection == TKPopupMenuArrowDirectionBottom) {
        self.videoToolView.frame = CGRectMake(_borderWidth, _borderWidth, frame.size.width - _borderWidth * 2, frame.size.height - _arrowHeight);
    }else if (_arrowDirection == TKPopupMenuArrowDirectionLeft) {
        self.videoToolView.frame = CGRectMake(_borderWidth + _arrowHeight, _borderWidth , frame.size.width - _borderWidth * 2 - _arrowHeight, frame.size.height);
    }else if (_arrowDirection == TKPopupMenuArrowDirectionRight) {
        self.videoToolView.frame = CGRectMake(_borderWidth , _borderWidth , frame.size.width - _borderWidth * 2 - _arrowHeight, frame.size.height);
    }
}

- (void)changeRectCorner
{
    if (_isCornerChanged || _rectCorner == UIRectCornerAllCorners) {
        return;
    }
    BOOL haveTopLeftCorner = NO, haveTopRightCorner = NO, haveBottomLeftCorner = NO, haveBottomRightCorner = NO;
    if (_rectCorner & UIRectCornerTopLeft) {
        haveTopLeftCorner = YES;
    }
    if (_rectCorner & UIRectCornerTopRight) {
        haveTopRightCorner = YES;
    }
    if (_rectCorner & UIRectCornerBottomLeft) {
        haveBottomLeftCorner = YES;
    }
    if (_rectCorner & UIRectCornerBottomRight) {
        haveBottomRightCorner = YES;
    }
    
    if (_arrowDirection == TKPopupMenuArrowDirectionTop || _arrowDirection == TKPopupMenuArrowDirectionBottom) {
        
        if (haveTopLeftCorner) {
            _rectCorner = _rectCorner | UIRectCornerBottomLeft;
        }else {
            _rectCorner = _rectCorner & (~UIRectCornerBottomLeft);
        }
        if (haveTopRightCorner) {
            _rectCorner = _rectCorner | UIRectCornerBottomRight;
        }else {
            _rectCorner = _rectCorner & (~UIRectCornerBottomRight);
        }
        if (haveBottomLeftCorner) {
            _rectCorner = _rectCorner | UIRectCornerTopLeft;
        }else {
            _rectCorner = _rectCorner & (~UIRectCornerTopLeft);
        }
        if (haveBottomRightCorner) {
            _rectCorner = _rectCorner | UIRectCornerTopRight;
        }else {
            _rectCorner = _rectCorner & (~UIRectCornerTopRight);
        }
        
    }else if (_arrowDirection == TKPopupMenuArrowDirectionLeft || _arrowDirection == TKPopupMenuArrowDirectionRight) {
        if (haveTopLeftCorner) {
            _rectCorner = _rectCorner | UIRectCornerTopRight;
        }else {
            _rectCorner = _rectCorner & (~UIRectCornerTopRight);
        }
        if (haveTopRightCorner) {
            _rectCorner = _rectCorner | UIRectCornerTopLeft;
        }else {
            _rectCorner = _rectCorner & (~UIRectCornerTopLeft);
        }
        if (haveBottomLeftCorner) {
            _rectCorner = _rectCorner | UIRectCornerBottomRight;
        }else {
            _rectCorner = _rectCorner & (~UIRectCornerBottomRight);
        }
        if (haveBottomRightCorner) {
            _rectCorner = _rectCorner | UIRectCornerBottomLeft;
        }else {
            _rectCorner = _rectCorner & (~UIRectCornerBottomLeft);
        }
    }
    
    _isCornerChanged = YES;
}

- (void)setOffset
{
    if (_itemWidth == 0) return;
    
    CGRect originRect = self.frame;
    
    if (_arrowDirection == TKPopupMenuArrowDirectionTop) {
        originRect.origin.y += _offset;
    }else if (_arrowDirection == TKPopupMenuArrowDirectionBottom) {
        originRect.origin.y -= _offset;
    }else if (_arrowDirection == TKPopupMenuArrowDirectionLeft) {
        originRect.origin.x += _offset;
    }else if (_arrowDirection == TKPopupMenuArrowDirectionRight) {
        originRect.origin.x -= _offset;
    }
    self.frame = originRect;
}

//设置锚点，关系到显示的动画，变大起始点即为最后设置的锚点
- (void)setAnchorPoint
{
    if (_itemWidth == 0) return;
    
    CGPoint point = CGPointMake(0.5, 0.5);
    if (_arrowDirection == TKPopupMenuArrowDirectionTop) {
        point = CGPointMake(_arrowPosition / _itemWidth, 0);
    }else if (_arrowDirection == TKPopupMenuArrowDirectionBottom) {
        point = CGPointMake(_arrowPosition / _itemWidth, 1);
    }else if (_arrowDirection == TKPopupMenuArrowDirectionLeft) {
        point = CGPointMake(0, (_itemHeight - _arrowPosition) / _itemHeight);
    }else if (_arrowDirection == TKPopupMenuArrowDirectionRight) {
        point = CGPointMake(1, (_itemHeight - _arrowPosition) / _itemHeight);
    }
    CGRect originRect = self.frame;
    self.layer.anchorPoint = point;
    self.frame = originRect;
}

- (void)setArrowPosition
{
    if (_priorityDirection == TKPopupMenuPriorityDirectionNone) {
        return;
    }
    if (_arrowDirection == TKPopupMenuArrowDirectionTop || _arrowDirection == TKPopupMenuArrowDirectionBottom) {
        if (_point.x + _itemWidth / 2 > ScreenW - _minSpace) {
            _arrowPosition = _itemWidth - (ScreenW - _minSpace - _point.x);
        }else if (_point.x < _itemWidth / 2 + _minSpace) {
            _arrowPosition = _point.x - _minSpace;
        }else {
            _arrowPosition = _itemWidth / 2;
        }
        
    }
}

- (void)drawRect:(CGRect)rect
{
    //主要设置箭头样式和位置
    UIBezierPath *bezierPath = [TKPopupMenuPath yb_bezierPathWithRect:rect rectCorner:_rectCorner cornerRadius:_cornerRadius borderWidth:_borderWidth borderColor:_borderColor backgroundColor:_backColor arrowWidth:_arrowWidth arrowHeight:_arrowHeight arrowPosition:_arrowPosition arrowDirection:_arrowDirection];
    
    [bezierPath fill];
    [bezierPath stroke];
}

@end
