//
//  TKMediaMarkView.m
//  TKWhiteBoard
//
//  Created by 周洁 on 2019/1/17.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import "TKMediaMarkView.h"
#import "TKBrushSelectorView.h"
#import <TKRoomSDK/TKRoomSDK.h>
#import "TKEduSessionHandle.h"

#define ThemeKP(args) [@"TKNativeWB.LightWB." stringByAppendingString:args]

@interface TKMediaMarkView ()<TKBrushSelectorViewDelegate>

@end

@implementation TKMediaMarkView
{
    UIButton *_penBtn;
    UIButton *_eraserBtn;
    
    TKBrushSelectorView *_selectorView;
}

- (instancetype)init
{
    if (self = [super init]) {
        _recoveryArray = [@[] mutableCopy];
        [self layout];
    }
    
    return self;
}

- (void)layout
{
    _tkDrawView = [[TKDrawView alloc] initWithDelegate:self];
    [self addSubview:_tkDrawView];
    [_tkDrawView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.mas_left);
        make.right.equalTo(self.mas_right);
        make.top.equalTo(self.mas_top);
        make.bottom.equalTo(self.mas_bottom);
    }];
    [_tkDrawView switchToFileID:@"videoDrawBoard" pageID:1 refreshImmediately:YES];
    
    _eraserBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _eraserBtn.sakura.backgroundImage(ThemeKP(@"tk_xiangpi_default_mark"), UIControlStateNormal);
    _eraserBtn.sakura.backgroundImage(ThemeKP(@"tk_xiangpi_press_mark"), UIControlStateSelected);
    [_eraserBtn addTarget:self action:@selector(chooseEraser) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_eraserBtn];
    [_eraserBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.mas_left).offset(8 * Proportion);
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(51 * Proportion, 51 * Proportion)]);
        make.centerY.equalTo(self.mas_centerY);
    }];
    
    _penBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _penBtn.selected = YES;
    _penBtn.sakura.backgroundImage(ThemeKP(@"tk_pen_default_mark"), UIControlStateNormal);
    _penBtn.sakura.backgroundImage(ThemeKP(@"tk_pen_press_mark"), UIControlStateSelected);
    [_penBtn addTarget:self action:@selector(choosePen) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_penBtn];
    [_penBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.mas_left).offset(8 * Proportion);
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(51 * Proportion, 51 * Proportion)]);
        make.bottom.equalTo(_eraserBtn.mas_top).offset(-25 * Proportion);
    }];
    
    _exitBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _exitBtn.sakura.backgroundImage(ThemeKP(@"tk_tuichu"), UIControlStateNormal);
    [_exitBtn addTarget:self action:@selector(chooseExit) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_exitBtn];
    [_exitBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.mas_left).offset(8 * Proportion);
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(51 * Proportion, 51 * Proportion)]);
        make.top.equalTo(_eraserBtn.mas_bottom).offset(25 * Proportion);
    }];
    
    _selectorView = [[TKBrushSelectorView alloc] initWithDefaultColor:@"#68C1FF"];
    _selectorView.clipsToBounds = YES;
    _selectorView.delegate = self;
    
    [self setNeedsLayout];
    [self layoutIfNeeded];
    
    _exitBtn.hidden = _eraserBtn.hidden = _penBtn.hidden = !([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher);
    [_tkDrawView setWorkMode:([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) ? TKWorkModeControllor : TKWorkModeViewer];
}

- (void)setDefaultDrawPen
{
    [self brushSelectorViewDidSelectDrawType:TKDrawTypePen color:@"#5AC9FA" widthProgress:0.05f];
}

- (void)chooseEraser
{
    _eraserBtn.selected = YES;
    _penBtn.selected = NO;
    
    [_selectorView showType:TKSelectorShowTypeLow];
    [_selectorView showOnMediaMarkViewRightToView:_eraserBtn type:TKBrushToolTypeEraser];
}

- (void)choosePen
{
    _penBtn.selected = YES;
    _eraserBtn.selected = NO;
    
    [_selectorView showType:TKSelectorShowTypeMiddle];
    [_selectorView showOnMediaMarkViewRightToView:_penBtn type:TKBrushToolTypeLine];
}

- (void)chooseExit
{
    _eraserBtn.selected = NO;
    _penBtn.selected = NO;
    [_selectorView removeFromSuperview];
}

- (void)setVideoRatio:(NSNumber *)videoRatio
{
    _videoRatio = videoRatio;
    if (!self.superview) {
        return;
    }
    float screnRatio = [UIScreen mainScreen].bounds.size.width / [UIScreen mainScreen].bounds.size.height;
    if (_videoRatio.floatValue >= screnRatio) {
        [self mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.superview.mas_left);
            make.right.equalTo(self.superview.mas_right);
            make.centerY.equalTo(self.superview.mas_centerY);
            make.height.equalTo(self.superview.mas_width).multipliedBy(1 / _videoRatio.floatValue);
        }];
    } else {
        [self mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.superview.mas_top);
            make.bottom.equalTo(self.superview.mas_bottom);
            make.centerX.equalTo(self.superview.mas_centerX);
            make.width.equalTo(self.superview.mas_height).multipliedBy(_videoRatio.floatValue);
        }];
    }
}

- (void)didMoveToSuperview
{
    if (_videoRatio != nil) {
        [self setVideoRatio:_videoRatio];
    }
}

//选择画笔工具回调数据
- (void)brushSelectorViewDidSelectDrawType:(TKDrawType)type color:(NSString *)hexColor widthProgress:(float)progress
{
    [_tkDrawView setDrawType:type hexColor:hexColor progress:progress];
}

- (void)setHidden:(BOOL)hidden
{
    if (hidden) {
        [_tkDrawView clearDataAfterClass];
    } else {
        if (self.hidden) {
            [self setDefaultDrawPen];
            _penBtn.selected = YES;
            _eraserBtn.enabled = NO;
        }
    }
    [super setHidden:hidden];
}

- (void)addSharpWithFileID:(NSString *)fileid shapeID:(NSString *)shapeID shapeData:(NSData *)shapeData
{
    if ([TKEduSessionHandle shareInstance].localUser.role != TKUserType_Teacher) {
        return;
    }
    
    NSMutableDictionary *dic = [NSJSONSerialization JSONObjectWithData:shapeData options:NSJSONReadingMutableContainers error:nil];
    
    [dic setObject:_tkDrawView.fileid forKey:@"whiteboardID"];
    [dic setObject:@(NO) forKey:@"isBaseboard"];
    
    [dic setObject:[TKEduSessionHandle shareInstance].localUser.nickName forKey:@"nickname"];
    
    NSData *newData = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:nil];
    NSString *data = [[NSString alloc] initWithData:newData encoding:NSUTF8StringEncoding];
    NSString *s1 = [data stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sSharpsChange ID:shapeID To:sTellAll Data:s1 Save:YES AssociatedMsgID:sVideoWhiteboard AssociatedUserID:nil expires:0 completion:nil];
}

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event
{
    if (!_eraserBtn.enabled) {
        if ([_eraserBtn pointInside:[self convertPoint:point toView:_eraserBtn] withEvent:event]) {
            return NO;
        }
    }
    return YES;
}

- (void)clear
{
    _eraserBtn.enabled = NO;
}

- (void)handleSignal:(NSDictionary *)dictionary isDel:(BOOL)isDel
{
    if (!dictionary || dictionary.count == 0) {
        return;
    }
    
    //信令相关性
//    NSString *associatedMsgID = [dictionary objectForKey:sAssociatedMsgID];
    
    //信令名
    NSString *msgName = [dictionary objectForKey:sName];
    
    //信令内容
    id dataObject = [dictionary objectForKey:@"data"];
    NSMutableDictionary *data = nil;
    if ([dataObject isKindOfClass:[NSDictionary class]]) {
        data = [NSMutableDictionary dictionaryWithDictionary:dataObject];
    }
    if ([dataObject isKindOfClass:[NSString class]]) {
        data = [NSJSONSerialization JSONObjectWithData:[(NSString *)dataObject dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:nil];
    }
    
    if ([msgName isEqualToString:sVideoWhiteboard]) {
        if (!isDel) {
            NSNumber *videoRatio;
            if ([data isKindOfClass:[NSDictionary class]]) {
                videoRatio = [(NSDictionary *)(data) objectForKey:@"videoRatio"];
            }
            if ([data isKindOfClass:[NSString class]]) {
                videoRatio = [[NSJSONSerialization JSONObjectWithData:[(NSString *)data dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableLeaves error:nil] objectForKey:@"videoRatio"];
            }
            [TKEduSessionHandle shareInstance].mediaMarkView.videoRatio = videoRatio;
            [TKEduSessionHandle shareInstance].mediaMarkView.hidden = NO;
            return;
        }
    }
    
    //MARK: 视频标注绘制
    id whiteboardID = [data objectForKey:@"whiteboardID"];
    if ([whiteboardID isKindOfClass:NSString.class]) {
        
        if ([whiteboardID isEqualToString:@"videoDrawBoard"]) {
            [[TKEduSessionHandle shareInstance].mediaMarkView.tkDrawView switchToFileID:whiteboardID pageID:1 refreshImmediately:YES];
            [[TKEduSessionHandle shareInstance].mediaMarkView.tkDrawView addDrawData:data refreshImmediately:YES];
            [TKEduSessionHandle shareInstance].mediaMarkView.eraserBtn.enabled = YES;
            return;
        }
    }
    
    return;
}

- (void)recoveryMediaMark
{
    //开始恢复视频标注数据
    if (_recoveryArray.count != 0) {
        _recoveryArray = [[_recoveryArray sortedArrayUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
            return [[obj1 objectForKey:@"seq"] compare:[obj2 objectForKey:@"seq"]];
        }] mutableCopy];
        
        [_recoveryArray enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {

            [self handleSignal:obj isDel:NO];
        }];
    }
    
    [_recoveryArray removeAllObjects];
}

@end
