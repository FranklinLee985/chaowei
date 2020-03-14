//
//  TKNativeWBToolView.h
//  WhiteBoardTools
//
//  Created by 周洁 on 2018/12/25.
//  Copyright © 2018 周洁. All rights reserved.
//	工具条

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol TKBrushToolViewDelegate <NSObject>

- (void)brushToolViewDidSelect:(TKBrushToolType)type;

@end


@interface TKBrushToolView : UIView

@property (nonatomic, strong) UIButton *toolsBtn;
@property (nonatomic, strong) UIButton *mouseBtn;
@property (nonatomic, strong) UIButton *penBtn;
@property (nonatomic, strong) UIButton *textBtn;
@property (nonatomic, strong) UIButton *shapeBtn;
@property (nonatomic, strong) UIButton *eraserBtn;
@property (nonatomic, strong) UIButton *closeBtn;
@property (nonatomic, strong) UIButton *curBtn;
@property (nonatomic, assign) int fromMouseToTool;

@property (nonatomic, weak) id<TKBrushToolViewDelegate>delegate;

- (void)chooseFromRemote:(UIButton *)btn;

- (void)setConfig:(TKRoomConfiguration *)config;

- (void)hideSelectorView;
@end

NS_ASSUME_NONNULL_END
