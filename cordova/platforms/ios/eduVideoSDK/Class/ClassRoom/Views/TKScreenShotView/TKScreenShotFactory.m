//
//  TKScreenShotFactory.m
//  EduClass
//
//  Created by Yibo on 2019/4/8.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKScreenShotFactory.h"



@interface TKScreenShotView()<TKBrushToolViewDelegate>


@end
@implementation TKScreenShotFactory
{
    TKDrawType _lastType;
    NSString *_lastHexColor;
    float _lastProgress;
    UIImageView *_laser;
}

static TKScreenShotFactory *f = nil;
+ (instancetype)sharedFactory
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        f = [[TKScreenShotFactory alloc] init];
        f.screenshotDic = [NSMutableDictionary dictionary];
    });
    return f;
}

+ (void)handleSignal:(NSDictionary *)dictionary isDel:(BOOL)isDel
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
    
    [self captureImgWithParam:dictionary msgName:msgName delete:isDel];

    return;
}

+ (void)captureImgWithParam:(NSDictionary *)param msgName:(NSString *) msgName delete:(BOOL)state;
{
    if (!f.contentView) {
        return;
    }
    if ([TKWhiteBoardManager shareInstance].address == nil) {
        
        return;
    }

    
    if (state == YES) { // 删除
       
        TKScreenShotView *ssView = [f.screenshotDic objectForKey:param[@"id"]];
        if (ssView) {
            
            [ssView removeFromSuperview];
            [f.screenshotDic removeObjectForKey:param[@"id"]];
        }
        
        return;
    }
    
    NSDictionary *paramData = @{};
    if ([param[@"data"] isKindOfClass:[NSString class]]) {
        NSString *tDataString = [NSString stringWithFormat:@"%@",param[@"data"]];
        NSData *tJsData = [tDataString dataUsingEncoding:NSUTF8StringEncoding];
        paramData = [NSJSONSerialization JSONObjectWithData:tJsData options:NSJSONReadingMutableContainers error:nil];
    }
    else if ([param[@"data"] isKindOfClass:[NSDictionary class]]) {
        paramData = (NSDictionary *)param[@"data"];
    }
    
    
    if ([msgName isEqualToString:@"CaptureImg"]) {
        
        NSDictionary *captureInfo    = paramData[@"captureImgInfo"];
        
        if (captureInfo && !state) {// 添加
            NSMutableString *downloadpath = [NSMutableString stringWithFormat:@"%@", captureInfo[@"downloadpath"]];
            [downloadpath insertString:@"-1" atIndex: [downloadpath rangeOfString:@"."].location];// 文件名需要加-1
            NSString *path = [NSString stringWithFormat:@"https://%@%@", [TKWhiteBoardManager shareInstance].address, downloadpath];
            NSDictionary *remSize = [paramData objectForKey:@"remSize"];
            NSString *captureID = [NSString stringWithFormat:@"CaptureImg_%@" ,captureInfo[@"fileid"]];
            TKScreenShotView *ssView = [[TKScreenShotView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(f.contentView.frame), CGRectGetHeight(f.contentView.frame))
                                                                          path:path
                                                                     superView:f.contentView
                                                                     captureID:captureID
                                                                       remSize:remSize];
            
            
            ssView.center    = f.contentView.center;
            [f.screenshotDic setObject:ssView forKey:captureID];
            [ssView setNeedsLayout];
            [ssView layoutIfNeeded];
            [ssView.tkDrawView setDrawType:f->_lastType hexColor:f->_lastHexColor progress:f->_lastProgress];
        
            [ssView.tkDrawView
             setWorkMode: (f.canDraw == NO) ? TKWorkModeViewer : TKWorkModeControllor];
//            [ssView.tkDrawView setWorkMode:TKWorkModeViewer];
        }
       
    }
    else if ([msgName isEqualToString:@"CaptureImgResize"]) { // 改变尺寸
        
        TKScreenShotView *ssView = [f.screenshotDic objectForKey:param[@"associatedMsgID"]];
        if (ssView) {
            [ssView changeScale:[paramData[@"scale"] floatValue]];
        }
    }
    else if ([msgName isEqualToString:@"CaptureImgDrag"]) { // 拖拽
        
        TKScreenShotView *ssView = [f.screenshotDic objectForKey:param[@"associatedMsgID"]];
        if (ssView) {
            
            NSDictionary *captureInfo    = paramData[@"position"];
            [ssView moveToLeft:captureInfo[@"percentLeft"] top:captureInfo[@"percentTop"]];
        }
    }
    else if ([msgName isEqualToString:sSharpsChange]) {
        NSString *associatedMsgID = [param objectForKey:@"associatedMsgID"];
        if ([f.screenshotDic objectForKey:associatedMsgID]) {// 截屏绘制
            //信令内容
            id dataObject = [param objectForKey:@"data"];
            NSMutableDictionary *data = nil;
            if ([dataObject isKindOfClass:[NSDictionary class]]) {
                data = [NSMutableDictionary dictionaryWithDictionary:dataObject];
            }
            if ([dataObject isKindOfClass:[NSString class]]) {
                data = [NSJSONSerialization JSONObjectWithData:[(NSString *)dataObject dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:nil];
            }
            TKScreenShotView *ssV = [f.screenshotDic objectForKey:associatedMsgID];
            NSString *eventType = [data objectForKey:@"eventType"];
            if ([eventType isEqualToString:@"laserMarkEvent"]) {
                [f laserPen:data onView:ssV];
            } else {
                [ssV.tkDrawView addDrawData:data refreshImmediately:YES];
            }
            [ssV.superview bringSubviewToFront:ssV];
        }
    }
}

// 激光笔
- (void)laserPen:(NSDictionary *)dic onView:(UIView *)view
{
    if ([dic[@"actionName"] isEqualToString:@"show"]) {// 显示
        if (!_laser) {
            CGFloat width = IS_PAD ? 40. : 20.;
            _laser = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, width, width)];
            _laser.image = [UIImage imageNamed:@"tk_laser_point"];
        }
    }
    else if ([dic[@"actionName"] isEqualToString:@"move"]) {// 移动
        if (dic[@"laser"][@"top"] && dic[@"laser"][@"left"]) {
            
            CGFloat x    = [dic[@"laser"][@"left"] floatValue] / 100.;
            CGFloat y    = [dic[@"laser"][@"top"] floatValue] / 100.;
            CGRect drawRect = view.frame;
            
            if (!_laser.superview) {
                [view addSubview:_laser];
            }
            [_laser mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.centerX.equalTo(view.mas_left).offset(drawRect.size.width * x);
                make.centerY.equalTo(view.mas_top).offset(drawRect.size.height * y);
            }];
        }
    }
    else if ([dic[@"actionName"] isEqualToString:@"hide"])  { // 隐藏
        [_laser removeFromSuperview];
        _laser = nil;
    }
}


+ (void)clearAfterClass
{
    [f.screenshotDic enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull key, TKScreenShotView * _Nonnull obj, BOOL * _Nonnull stop) {
        [obj removeFromSuperview];
    }];
    
    [f.screenshotDic removeAllObjects];
}

- (void)setDrawType:(TKDrawType)type color:(NSString *)hexColor progress:(float)progress
{
    _lastType = type;
    _lastHexColor = hexColor;
    _lastProgress = progress;
    [f.screenshotDic enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull key, TKScreenShotView * _Nonnull obj, BOOL * _Nonnull stop) {
        
        [obj.tkDrawView setDrawType:type hexColor:hexColor progress:progress];
    }];
}
- (void)setBrush:(TKBrushToolView *)brush {

        _brush = brush;
        _brush.delegate = (id)self;
    
}

- (void)setCanDraw:(BOOL)canDraw {
    if (_canDraw != canDraw) {
        
        _canDraw = canDraw;
        
        for (NSString *key in f.screenshotDic) {
            [f.screenshotDic[key].tkDrawView
             setWorkMode: _canDraw ? ((_toolType == TKBrushToolTypeMouse) ? TKWorkModeViewer : TKWorkModeControllor) : TKWorkModeViewer];
        }
    }
}

- (void)brushToolViewDidSelect:(TKBrushToolType)type {

    _toolType = type;
    for (NSString *key in f.screenshotDic) {
        [f.screenshotDic[key].tkDrawView
         setWorkMode: _canDraw ? ((_toolType == TKBrushToolTypeMouse) ? TKWorkModeViewer : TKWorkModeControllor) : TKWorkModeViewer];
    }
}
@end
