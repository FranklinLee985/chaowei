//
//  TKNativeWBSelectorView.m
//  WhiteBoardTools
//
//  Created by 周洁 on 2018/12/25.
//  Copyright © 2018 周洁. All rights reserved.
//
//#define MAS_SHORTHAND
//#define MAS_SHORTHAND_GLOBALS
#import "TKBrushSelectorView.h"
#import "TKEduSessionHandle.h"
#import "TKColorSelectorView.h"

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wimplicit-retain-self"

#define ThemeKP(args) [@"TKNativeWB.ColorSelectorTools." stringByAppendingString:args]

@implementation TKBrushSelectorView
{
    UIButton    *_tool1;
    UIButton    *_tool2;
    UIButton    *_tool3;
    UIButton    *_tool4;
    
    UIButton    *_msTextBtn;
    
    UIView      *_line;
    
    float       _paddingToEdge;
    float       _paddingToItem;
    
    NSArray <UIButton *>*_tools;
    NSMutableArray <UIButton *>*_colorBtns;
    
    UIImageView      *_progressView;
    
    TKBrushToolType    _type;
    
    
    
    UIView              *_cutView;
    
    NSMutableDictionary *_dataDictionary;//用来记录选择
    
    UISlider *_slider;
    
    CGFloat _progress1;//画线工具的进度
    CGFloat _progress2;//文字工具的进度
    CGFloat _progress3;//图形工具的进度
    CGFloat _progress4;//橡皮工具的进度
    
    TKColorSelectorView * _colorSelectView;
    CGFloat iphonePropor;
}

- (instancetype)initWithDefaultColor:(nullable NSString *)color
{
    if (self = [super init]) {
        
        _dataDictionary = [NSMutableDictionary dictionary];
        self.clipsToBounds = YES;
        //        [self setType:TKBrushToolTypeLine];
        
        _currentColor = color ?: @"#000000";
        iphonePropor = fmaxf(Proportion, 0.68);
        
        [self layout];
        
        //默认值
        _drawType = TKDrawTypePen;
        
        _progress1 = 0.03f;
        _progress2 = 0.3f;
        _progress3 = 0.03f;
        _progress4 = 0.03f;

    }
    return self;
}


- (void)layout
{
    self.backgroundColor = [[TKTheme colorWithPath:ThemeKP(@"bg_color")] colorWithAlphaComponent:[TKTheme floatWithPath:ThemeKP(@"bg_alpha")]];
    self.layer.masksToBounds = YES;
    self.layer.cornerRadius = 10 * Proportion;
    
    _cutView = [[UIView alloc] init];
    _cutView.backgroundColor = UIColor.clearColor;
    [self addSubview:_cutView];
    [_cutView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.mas_left);
        make.right.equalTo(self.mas_right);
        make.bottom.equalTo(self.mas_bottom);
    }];
    
    
    _tool1 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tool1.sakura.image(ThemeKP(@"tk_pen_default"), UIControlStateNormal);
    _tool1.sakura.image(ThemeKP(@"tk_pen_selected"), UIControlStateSelected);
    
    _tool2 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tool2.sakura.image(ThemeKP(@"tk_yingguangbi_default"), UIControlStateNormal);
    _tool2.sakura.image(ThemeKP(@"tk_yingguangbi_selected"), UIControlStateSelected);
    
    _tool3 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tool3.sakura.image(ThemeKP(@"tk_line_default"), UIControlStateNormal);
    _tool3.sakura.image(ThemeKP(@"tk_line_selected"), UIControlStateSelected);
    
    _tool4 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tool4.sakura.image(ThemeKP(@"tk_jiantou_default"), UIControlStateNormal);
    _tool4.sakura.image(ThemeKP(@"tk_jiantou_selected"), UIControlStateSelected);
    
    _tools = @[_tool1, _tool2, _tool3, _tool4];
    UIButton *lastBtn = nil;
    for (int i = 0; i < _tools.count; i++) {
        UIButton *btn = _tools[i];
        [_cutView addSubview:btn];
        [btn addTarget:self action:@selector(toolsSelect:) forControlEvents:UIControlEventTouchUpInside];
        [btn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(25 * iphonePropor, 25 * iphonePropor)]);
            make.left.equalTo(lastBtn ? lastBtn.mas_right : _cutView.mas_left).offset(lastBtn ? 33 * iphonePropor : 28 * iphonePropor);
            make.top.equalTo(_cutView.mas_top).offset(14 * iphonePropor);
            if (i == _tools.count - 1) {
                make.right.equalTo(_cutView.mas_right).offset(-33 * iphonePropor);
            }
        }];
        lastBtn = btn;
    }
    
    _msTextBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [_msTextBtn setAttributedTitle:[[NSAttributedString alloc] initWithString:@"默认字体" attributes:@{NSFontAttributeName : [UIFont systemFontOfSize:13 * iphonePropor], NSForegroundColorAttributeName : [UIColor colorWithRed:255 / 255.0f green:220 / 255.0f blue:84 / 255.0f alpha:1]}] forState:UIControlStateNormal];
    _msTextBtn.layer.masksToBounds = YES;
    _msTextBtn.layer.cornerRadius = 5 * iphonePropor;
    _msTextBtn.layer.borderWidth = 2;
    _msTextBtn.layer.borderColor = [UIColor colorWithRed:255 / 255.0f green:220 / 255.0f blue:84 / 255.0f alpha:1].CGColor;
    [_cutView addSubview:_msTextBtn];
    [_msTextBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(_cutView).offset(10 * iphonePropor);
        make.top.equalTo(_cutView).offset(12 * iphonePropor);
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(68 * iphonePropor, 30 * iphonePropor)]).priorityHigh();
    }];
    _msTextBtn.hidden = YES;
    
    _line = [[UIView alloc] init];
    _line.backgroundColor = UIColor.lightGrayColor;
    [_cutView addSubview:_line];
    [_line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(_cutView).offset(10 * iphonePropor);
        make.right.equalTo(_cutView).offset(-10 * iphonePropor);
        make.top.equalTo(_tool1.mas_bottom).offset(14 * iphonePropor);
        make.height.equalTo(@(1));
    }];
    
    /*
    NSArray *nImages = @[@"tk_blue_default",
                         @"tk_yellow_default",
                         @"tk_red_default",
                         @"tk_purple_default",
                         @"tk_DeepBlue_default",
                         @"tk_green_default",
                         @"tk_black_default",
                         @"tk_grey_default"];
    
    NSArray *sImages = @[@"tk_blue_selected",
                         @"tk_yellow_selected",
                         @"tk_red_selected",
                         @"tk_purple_selected",
                         @"tk_DeepBlue_selected",
                         @"tk_green_selected",
                         @"tk_black_selected",
                         @"tk_grey_selected"];
    _colorBtns = [@[] mutableCopy];
    for (int i = 0; i < nImages.count; i++) {
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_colorBtns addObject:btn];
        btn.sakura.image(ThemeKP(nImages[i]), UIControlStateNormal);
        btn.sakura.image(ThemeKP(sImages[i]), UIControlStateSelected);

        [_cutView addSubview:btn];
        [btn addTarget:self action:@selector(changeColor:) forControlEvents:UIControlEventTouchUpInside];
        [btn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(35 * iphonePropor, 35 * iphonePropor)]).priorityHigh();
            make.top.equalTo(_line).offset(((i / 4.0) >= 1) ? (19 + 35 + 23) * iphonePropor : 19 * iphonePropor);
            make.centerX.equalTo(_tools[i % 4]);
        }];
        
        btn.hidden = YES;
    }*/
    
#pragma mark - color selector view
    
    _colorSelectView = ({
        TKColorSelectorView * colorView = [[TKColorSelectorView alloc] init];
        [colorView setCurrentSelectColor:_currentColor];
        [_cutView addSubview:colorView];
        [colorView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(_cutView).offset(10 * iphonePropor);
            make.right.equalTo(_cutView).offset(-10 * iphonePropor);
            make.top.equalTo(_line.mas_bottom).offset(29 * iphonePropor);
            make.height.equalTo(@(21 * iphonePropor));
        }];
        colorView.chooseBackBlock = ^(NSString * _Nonnull colorStr) {
            
            if (self.delegate && [self.delegate respondsToSelector:@selector(brushSelectorViewDidSelectDrawType:color:widthProgress:)]) {
                [self.delegate brushSelectorViewDidSelectDrawType:_drawType color:colorStr widthProgress:[self lineWidth]];
            }
            self.currentColor = colorStr;
        };
        colorView;
    });
    
    
    _progressView = [[UIImageView alloc] init];
    _progressView.sakura.image(ThemeKP(@"tk_control"));
    _progressView.layer.masksToBounds = YES;
    _progressView.layer.cornerRadius = 2.5f;
    [_cutView addSubview:_progressView];
    [_progressView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(_cutView.mas_left).offset(17 * iphonePropor);
        make.right.equalTo(_cutView.mas_right).offset(-17 * iphonePropor);
        make.height.equalTo(@(18 * iphonePropor));
        make.top.equalTo(_colorSelectView.mas_bottom).offset(29 * iphonePropor);
        make.bottom.equalTo(_cutView.mas_bottom).offset(-24 * iphonePropor);
    }];
    
    _slider = [[UISlider alloc] init];
    _slider.minimumTrackTintColor = [UIColor clearColor];
    _slider.maximumTrackTintColor = [UIColor clearColor];
    [_slider setThumbImage:[UIImage imageNamed:[TKTheme stringWithPath:ThemeKP(@"tk_control_slider")]] forState:UIControlStateNormal];
    _slider.minimumValue = 0.03f;
    _slider.maximumValue = 1.0;
    _slider.value = 0.45f;
    [_slider addTarget:self action:@selector(sliderValueChanged:) forControlEvents:UIControlEventValueChanged];

    [_cutView addSubview:_slider];

    [_slider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(30 * iphonePropor));
        make.centerY.equalTo(_progressView);
        make.left.equalTo(_progressView.mas_left);
        make.right.equalTo(_progressView.mas_right);
    }];
    
    _tool1.selected = YES;
    _colorBtns.firstObject.selected = YES;

}

- (void)sliderValueChanged:(id)sender
{
    switch (_type) {
        case TKBrushToolTypeLine:
        {
            _progress1 = _slider.value;
        }
            break;
        case TKBrushToolTypeText:
        {
            _progress2 = _slider.value;
        }
            break;
        case TKBrushToolTypeShape:
        {
            _progress3 = _slider.value;

        }
            break;
        case TKBrushToolTypeEraser:
        {
            _progress4 = _slider.value;
        }
            break;
            
        default:
            break;
    }
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(brushSelectorViewDidSelectDrawType:color:widthProgress:)]) {
        [self.delegate brushSelectorViewDidSelectDrawType:_drawType color:self.currentColor widthProgress:_slider.value];
    }
}

- (CGFloat)lineWidth
{
    CGFloat progress = 0.03f;
    switch (_type) {
        case TKBrushToolTypeLine:
        {
            progress = _progress1;
        }
            break;
        case TKBrushToolTypeText:
        {
            progress = _progress2;
        }
            break;
        case TKBrushToolTypeShape:
        {
            progress = _progress3;
        }
            break;
        case TKBrushToolTypeEraser:
        {
            progress = _progress4;
        }
            break;
            
        default:
            break;
    }
    return progress;
}

- (void)setLineWidth
{
    CGFloat progress = 0.03f;
    switch (_type) {
        case TKBrushToolTypeLine:
        {
            progress = _progress1;
        }
            break;
        case TKBrushToolTypeText:
        {
            progress = _progress2;
        }
            break;
        case TKBrushToolTypeShape:
        {
            progress = _progress3;
        }
            break;
        case TKBrushToolTypeEraser:
        {
            progress = _progress4;
        }
            break;
            
        default:
            break;
    }
    _slider.value = progress;
}

- (void)showType:(TKSelectorShowType)type
{
    switch (type) {
        case TKSelectorShowTypeHigh:
        {
            [self mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.left.equalTo(_cutView.mas_left);
                make.right.equalTo(_cutView.mas_right);
                make.top.equalTo(_cutView.mas_top);
                make.bottom.equalTo(_cutView.mas_bottom);
            }];
            break;
        }
        case TKSelectorShowTypeMiddle:
        {
            [self mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.left.equalTo(_cutView.mas_left);
                make.right.equalTo(_cutView.mas_right);
                make.top.equalTo(_line.mas_bottom);
                make.bottom.equalTo(_cutView.mas_bottom);
            }];
            break;
        }
        case TKSelectorShowTypeLow:
        {
            [self mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.left.equalTo(_cutView.mas_left);
                make.right.equalTo(_cutView.mas_right);
                make.top.equalTo(_progressView.mas_top).offset(-29 * iphonePropor);
                make.bottom.equalTo(_cutView.mas_bottom);
            }];
            break;
        }
        case TKSelectorShowTypeSpecial:
        {
            [self mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.left.equalTo(_cutView.mas_left);
                make.right.equalTo(_cutView.mas_right);
                make.top.equalTo(_line.mas_bottom);
                make.bottom.equalTo(_progressView.mas_top).offset(-12 *iphonePropor);
            }];
            break;
        }
            
        default:
            break;
    }
}

- (void)setType:(TKBrushToolType)type
{
    if (_type == type) {
        return;
    }

    _type = type;
    _drawType = (TKDrawType)type;
    [_tools enumerateObjectsUsingBlock:^(UIButton * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        obj.selected = NO;
    }];
    _tools.firstObject.selected = YES;

    if (type == TKBrushToolTypeLine) {
        _tool1.sakura.image(ThemeKP(@"tk_pen_default"), UIControlStateNormal);
        _tool1.sakura.image(ThemeKP(@"tk_pen_selected"), UIControlStateSelected);

        _tool2.sakura.image(ThemeKP(@"tk_yingguangbi_default"), UIControlStateNormal);
        _tool2.sakura.image(ThemeKP(@"tk_yingguangbi_selected"), UIControlStateSelected);
        
        _tool3.sakura.image(ThemeKP(@"tk_line_default"), UIControlStateNormal);
        _tool3.sakura.image(ThemeKP(@"tk_line_selected"), UIControlStateSelected);
        
        _tool4.sakura.image(ThemeKP(@"tk_jiantou_default"), UIControlStateNormal);
        _tool4.sakura.image(ThemeKP(@"tk_jiantou_selected"), UIControlStateSelected);

    }

    if (type == TKBrushToolTypeShape) {
        
        _tool1.sakura.image(ThemeKP(@"tk_kongxinjuxing_default"), UIControlStateNormal);
        _tool1.sakura.image(ThemeKP(@"tk_kongxinjuxing_selected"), UIControlStateSelected);
        
        _tool2.sakura.image(ThemeKP(@"tk_shixinjuxing"), UIControlStateNormal);
        _tool2.sakura.image(ThemeKP(@"tk_shixinjuxing_press"), UIControlStateSelected);
        
        _tool3.sakura.image(ThemeKP(@"tk_kongxinyuan_default"), UIControlStateNormal);
        _tool3.sakura.image(ThemeKP(@"tk_kongxinyuan_selected"), UIControlStateSelected);
        
        _tool4.sakura.image(ThemeKP(@"tk_shixinyuan_default"), UIControlStateNormal);
        _tool4.sakura.image(ThemeKP(@"tk_shixinyuan_selected"), UIControlStateSelected);

        [self setNeedsUpdateConstraints];
    }
    
    //根据画笔类型设置线条宽度
    [self setLineWidth];
}

//选画笔
- (void)toolsSelect:(UIButton *)button
{
    for (UIButton *tool in _tools) {
        tool.selected = NO;
    }
    button.selected = YES;
    _drawType = _type + [_tools indexOfObject:button];

    if (self.delegate && [self.delegate respondsToSelector:@selector(brushSelectorViewDidSelectDrawType:color:widthProgress:)]) {
        [self.delegate brushSelectorViewDidSelectDrawType:_drawType color:self.currentColor widthProgress:[self lineWidth]];
    }
    
//    [self removeFromSuperview];
}
/*
//选颜色
- (void)changeColor:(UIButton *)button
{
    for (UIButton *colorBtn in _colorBtns) {
        colorBtn.selected = NO;
    }
    button.selected = YES;
    
    self.currentColor = _colors[[_colorBtns indexOfObject:button]];
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(brushSelectorViewDidSelectDrawType:color:widthProgress:)]) {
        [self.delegate brushSelectorViewDidSelectDrawType:_drawType color:self.currentColor widthProgress:[self lineWidth]];
    }
    
    [[TKRoomManager instance] changeUserProperty:[TKRoomManager instance].localUser.peerID tellWhom:sTellAll data:@{@"primaryColor" : self.currentColor} completion:nil];
    
//    [self removeFromSuperview];
}
*/
- (void)showWithTKNativeWBToolView:(TKBrushToolView *)view type:(TKBrushToolType)type
{
    if (self.superview && type == _type) {
        [self removeFromSuperview];
        return;
    }
    
    if (type == TKBrushToolTypeMouse) {
        
        [self removeFromSuperview];
        return;
    }
    
    [self setType:type];
    
    [view.superview addSubview:self];

    //修改坐标
    UIButton *btn;
    if (_type == TKBrushToolTypeLine) {
        btn = view.penBtn;
    }
    else if (_type == TKBrushToolTypeText) {
        btn = view.textBtn;
    }
    else if (_type == TKBrushToolTypeShape) {
        btn = view.shapeBtn;
    }
    else if (_type == TKBrushToolTypeEraser) {
        btn = view.eraserBtn;
    }
    
    [self.superview setNeedsLayout];
    [self.superview layoutIfNeeded];
    
    [self mas_updateConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(btn.mas_centerY);
        make.right.equalTo(view.mas_left).offset(-10 * iphonePropor);
    }];
    
    //每次出现就应该调用delegate确定一个默认值
    if (self.delegate && [self.delegate respondsToSelector:@selector(brushSelectorViewDidSelectDrawType:color:widthProgress:)]) {
        [self.delegate brushSelectorViewDidSelectDrawType:_drawType color:self.currentColor widthProgress:[self lineWidth]];
        
//        if (self.currentColor) {
//            UIButton *curBtn    = [_colorBtns objectAtIndex: [_colors indexOfObject:self.currentColor]];
//            if (curBtn == nil) {
//                _colorBtns.firstObject.selected = YES;
//            }
//            else {
//                for (UIButton *colorBtn in _colorBtns) {
//                    colorBtn.selected = [curBtn isEqual:colorBtn] ? YES : NO;
//                }
//            }
//
//
//        }
    }
    
    [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:[TKEduSessionHandle shareInstance].localUser.peerID TellWhom:sTellAll data:@{@"primaryColor" : self.currentColor} completion:nil];
}

- (void)showOnMiniWhiteBoardAboveView:(UIView *)view type:(TKBrushToolType)type
{
    if (self.superview && type == _type) {
        [self removeFromSuperview];
        return;
    }
    
    [self setType:type];
    
    [view.superview.superview addSubview:self];
    
    [self.superview setNeedsLayout];
    [self.superview layoutIfNeeded];
    
    [self mas_updateConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(view.mas_centerX);//@(centerX)
        make.bottom.equalTo(view.mas_top).offset(-5 * iphonePropor);
    }];
    
    //每次出现就应该调用delegate确定一个默认值
    if (self.delegate && [self.delegate respondsToSelector:@selector(brushSelectorViewDidSelectDrawType:color:widthProgress:)]) {
        [self.delegate brushSelectorViewDidSelectDrawType:_drawType color:self.currentColor widthProgress:[self lineWidth]];
    }
}

- (void)showOnMediaMarkViewRightToView:(UIView *)view type:(TKBrushToolType)type
{
    if (self.superview && type == _type) {
        [self removeFromSuperview];
        return;
    }
    
    [self setType:type];
    
    [view.superview addSubview:self];
    
    [self.superview setNeedsLayout];
    [self.superview layoutIfNeeded];
    
    [self mas_updateConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(view.mas_centerY);//@(centerX)
        make.left.equalTo(view.mas_right).offset(10 * iphonePropor);
    }];
    
    //每次出现就应该调用delegate确定一个默认值
    if (self.delegate && [self.delegate respondsToSelector:@selector(brushSelectorViewDidSelectDrawType:color:widthProgress:)]) {
        [self.delegate brushSelectorViewDidSelectDrawType:_drawType color:self.currentColor widthProgress:[self lineWidth]];
    }

}

- (void)hide
{
    [self removeFromSuperview];
}

- (void)dealloc
{
    NSLog(@"dealloc--%@",self.class);
}
#pragma clang diagnostic pop
@end
