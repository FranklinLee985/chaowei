//
//  ScreenShotView.h
//  TKWhiteBoard
//
//  Created by Yi on 2019/2/20.
//  Copyright © 2019年 MAC-MiNi. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <TKWhiteBoard/TKDrawView.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKScreenShotView : UIImageView

@property (nonatomic, strong)TKDrawView *tkDrawView;

- (instancetype)initWithFrame:(CGRect)frame
                         path:(NSString *)path
                    superView:(UIView *)superView
                    captureID:(NSString *)captureID
                      remSize:(NSDictionary *)remSize;

- (void)moveToLeft:(id)left top:(id)top;

- (void)changeScale:(CGFloat)scale;

@end

NS_ASSUME_NONNULL_END
