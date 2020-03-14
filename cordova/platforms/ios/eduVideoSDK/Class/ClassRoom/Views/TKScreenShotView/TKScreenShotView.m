//
//  TKScreenShotView.m
//  TKWhiteBoard
//
//  Created by Yi on 2019/2/20.
//  Copyright © 2019年 MAC-MiNi. All rights reserved.
//

#import "TKScreenShotView.h"
#import "UIImageView+WebCache.h"
#import <TKWhiteBoard/TKDrawView.h>
#import "TKEduSessionHandle.h"
#import "TKScreenShotFactory.h"

#define screenViewFrameRatio 0.75

@interface TKScreenShotView() <TKDrawViewDelegate>
{
    id _top;
    id _left;
    BOOL _drag;
    
    UIView * sssSuperView;
}

@property (nonatomic, assign)CGSize   imgSize;// 图片原始尺寸
@property (nonatomic, assign)CGFloat  scale;
@property (nonatomic, assign)CGFloat  ratio; // 宽高比
@property (nonatomic, strong)NSString *captureID;
@property (nonatomic, strong)NSString *whiteboardID;
@property (nonatomic, assign)CGPoint startLocation;

@end

@implementation TKScreenShotView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (instancetype)initWithFrame:(CGRect)frame
                         path:(NSString *)path
                    superView:(nonnull UIView *)superView
                    captureID:(nonnull NSString *)captureID
                      remSize:(nonnull NSDictionary *)remSize
{

    self = [super initWithFrame:frame];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(brushToolMouseDidSelect:) name:@"TKBrushToolMouseSelected" object:nil];
        
        sssSuperView = superView;
        [sssSuperView addObserver:self forKeyPath:@"frame" options:NSKeyValueObservingOptionNew context:nil];
        
        self.userInteractionEnabled = YES;
        self.contentMode = UIViewContentModeScaleAspectFit;
        _scale    = 1;
        [superView addSubview:self];
        [self sd_setImageWithURL:[NSURL URLWithString:path] completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
            if (error) {
                TKLog(@"屏幕截屏图片下载失败, 清理self");
                [self removeFromSuperview];
            }
            else {
                if (image.size.width / image.size.height >= self.superview.frame.size.width / self.superview.frame.size.height) {
                    if (image.size.width >= self.superview.frame.size.width * screenViewFrameRatio) {
                        _imgSize = CGSizeMake(self.superview.frame.size.width * screenViewFrameRatio, self.superview.frame.size.width * screenViewFrameRatio * image.size.height / image.size.width);
                    } else {
                        _imgSize = image.size;
                    }
                } else {
                    if (image.size.height >= self.superview.frame.size.height * screenViewFrameRatio) {
                        _imgSize = CGSizeMake(self.superview.frame.size.height * screenViewFrameRatio * image.size.width / image.size.height, self.superview.frame.size.height * screenViewFrameRatio);
                    } else {
                        _imgSize = image.size;
                    }
                }
                
                [self mas_makeConstraints:^(MASConstraintMaker *make) {
                    make.size.equalTo([NSValue valueWithCGSize:_imgSize]);
                    make.width.lessThanOrEqualTo(superView.mas_width).priorityHigh();
                    make.height.lessThanOrEqualTo(superView.mas_height).priorityHigh();
                    make.centerY.equalTo(superView.mas_centerY);
                    make.centerX.equalTo(superView.mas_centerX);
                }];
                
                [self changeScale:_scale];
                if (_drag) {
                    [self moveToLeft:_left top:_top];
                }
                self.layer.borderWidth = 2.;
                self.layer.borderColor = UIColor.blueColor.CGColor;
            }
            
        }];
        
        self.captureID = captureID;
        self.whiteboardID = [NSString stringWithFormat:@"captureImgBoard%@",[captureID componentsSeparatedByString:@"_"].lastObject];;
        
        _tkDrawView = [[TKDrawView alloc] initWithDelegate:self];
        [_tkDrawView setWorkMode:[TKScreenShotFactory sharedFactory].canDraw ? (([TKScreenShotFactory sharedFactory].toolType == TKBrushToolTypeMouse) ? TKWorkModeViewer : TKWorkModeControllor) : TKWorkModeViewer];
        _tkDrawView.hidden = ([TKScreenShotFactory sharedFactory].toolType == TKBrushToolTypeMouse);
        [_tkDrawView switchToFileID:self.whiteboardID pageID:1 refreshImmediately:YES];
        [self addSubview:_tkDrawView];
        [_tkDrawView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(remoteSelectorTool:) name:TKWhiteBoardRemoteSelectTool
                                                   object:nil];

        
    }
    return self;
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    
    if ([keyPath isEqualToString:@"frame"]) {
        
        if (self.size.width / self.size.height >= self.superview.frame.size.width / self.superview.frame.size.height) {
            if (self.size.width >= self.superview.frame.size.width * screenViewFrameRatio) {
                _imgSize = CGSizeMake(self.superview.frame.size.width * screenViewFrameRatio, self.superview.frame.size.width * screenViewFrameRatio * self.size.height / self.size.width);
                
                [self mas_makeConstraints:^(MASConstraintMaker *make) {
                    make.size.equalTo([NSValue valueWithCGSize:_imgSize]);
                    make.width.lessThanOrEqualTo(self.superview.mas_width).priorityHigh();
                    make.height.lessThanOrEqualTo(self.superview.mas_height).priorityHigh();
                    make.centerY.equalTo(self.superview.mas_centerY);
                    make.centerX.equalTo(self.superview.mas_centerX);
                }];
            }
        } else {
            if (self.size.height >= self.superview.frame.size.height * screenViewFrameRatio) {
                _imgSize = CGSizeMake(self.superview.frame.size.height * screenViewFrameRatio * self.size.width / self.size.height, self.superview.frame.size.height * screenViewFrameRatio);
                
                [self mas_makeConstraints:^(MASConstraintMaker *make) {
                    make.size.equalTo([NSValue valueWithCGSize:_imgSize]);
                    make.width.lessThanOrEqualTo(self.superview.mas_width).priorityHigh();
                    make.height.lessThanOrEqualTo(self.superview.mas_height).priorityHigh();
                    make.centerY.equalTo(self.superview.mas_centerY);
                    make.centerX.equalTo(self.superview.mas_centerX);
                }];
            }
        }
    }
}

- (void)brushToolMouseDidSelect:(NSNotification *)noti
{
    NSNumber *selected = noti.object;
    _tkDrawView.hidden = selected.boolValue;
}

- (void)moveToLeft:(id)left top:(id)top {
    
    _left = left;
    _top = top;
    _drag = YES;
    
    CGFloat xPercent = [left floatValue];
    CGFloat yPercent = [top floatValue];

    if (self.superview) {
        CGSize newSize = CGSizeMake(_imgSize.width * _scale, _imgSize.height * _scale);
        if (newSize.width / newSize.height >= self.superview.frame.size.width / self.superview.frame.size.height) {
            if (newSize.width >= self.superview.frame.size.width) {
                newSize = CGSizeMake(self.superview.frame.size.width, self.superview.frame.size.width * newSize.height / newSize.width);
            } else {
                newSize = newSize;
            }
        } else {
            if (newSize.height >= self.superview.frame.size.height) {
                newSize = CGSizeMake(self.superview.frame.size.height * newSize.width / newSize.height, self.superview.frame.size.height);
            } else {
                newSize = newSize;
            }
        }
        float deltaCenterX = (self.superview.frame.size.width - newSize.width) * xPercent;
        float deltaCenterY = (self.superview.frame.size.height - newSize.height) * yPercent;
        
        [self mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(@(newSize.width));
            make.height.equalTo(@(newSize.height));
            
            make.centerX.equalTo(self.superview.mas_left).offset(deltaCenterX + newSize.width / 2);
            make.centerY.equalTo(self.superview.mas_top).offset(deltaCenterY + newSize.height / 2);
        }];
    }
    
}

- (void)changeScale:(CGFloat)scale {
    
    _scale = scale;
}

- (void)addSharpWithFileID:(NSString *)fileid shapeID:(NSString *)shapeID shapeData:(NSData *)shapeData
{
    NSMutableDictionary *dic = [NSJSONSerialization JSONObjectWithData:shapeData options:NSJSONReadingMutableContainers error:nil];
    [dic setObject:self.whiteboardID forKey:@"whiteboardID"];
    NSMutableDictionary *data = [dic objectForKey:@"data"];
    [dic setObject:data forKey:@"data"];
    NSString *str = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:nil] encoding:NSUTF8StringEncoding];
    
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sSharpsChange ID:shapeID To:sTellAll Data:[str stringByReplacingOccurrencesOfString:@"\n" withString:@""] Save:YES AssociatedMsgID:self.captureID AssociatedUserID:nil expires:0 completion:nil];
}

- (void)touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event
{
    // Calculate and store offset, and pop view into front if needed
    CGPoint pt = [[touches anyObject] locationInView:self];
    _startLocation = pt;
    [self.superview bringSubviewToFront:self];
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    // 计算偏移量
    CGPoint pt = [[touches anyObject] locationInView:self];
    
    float     dx = pt.x - _startLocation.x;
    float     dy = pt.y - _startLocation.y;

    CGPoint newcenter     = CGPointMake(self.center.x + dx, self.center.y + dy);
    // 设置移动的区域
    float halfx = CGRectGetMidX(self.bounds);
    newcenter.x = MAX(halfx, newcenter.x);
    newcenter.x = MIN(self.superview.bounds.size.width - halfx, newcenter.x);

    float halfy = CGRectGetMidY(self.bounds);
    newcenter.y = MAX(halfy, newcenter.y);
    newcenter.y = MIN(self.superview.bounds.size.height - halfy, newcenter.y);

    [self mas_updateConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.superview.mas_left).offset(newcenter.x);
        make.centerY.equalTo(self.superview.mas_top).offset(newcenter.y);
    }];
}
/*
- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    CGPoint pt = [[touches anyObject] locationInView:[self superview]];
    
    CGFloat offset_x = pt.x - _startLocation.x;
    CGFloat offset_y = pt.y - _startLocation.y;
    
    
    offset_x = MAX(0, offset_x);
    offset_x = MIN(CGRectGetWidth(self.superview.frame) - CGRectGetWidth(self.frame), offset_x);
    
    offset_y = MAX(0, offset_y);
    offset_y = MIN(CGRectGetHeight(self.superview.frame)-CGRectGetHeight(self.frame), offset_y);
    
    
    //重新设置中心点位置
    [self mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.superview.mas_left).offset(offset_x);
        make.top.equalTo(self.superview.mas_top).offset(offset_y);
    }];
    
}
 */

- (void)remoteSelectorTool:(NSNotification *)noti {
    // 有穿透画笔配置项不隐藏画布(笔迹)
    BOOL isPenCanPenetration = [TKEduClassRoom shareInstance].roomJson.configuration.isPenCanPenetration;
    
    if (isPenCanPenetration == NO) {
        
        BOOL isSelectMouse = [noti.object boolValue];
        _tkDrawView.hidden = isSelectMouse;
    }
}


- (void)dealloc
{
    [sssSuperView removeObserver:self forKeyPath:@"frame"];
    sssSuperView = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
