//
//  TKControlButton.m
//  EduClass
//
//  Created by lyy on 2018/5/2.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKControlButton.h"
#define ThemeKP(args) [@"ClassRoom.TKControlView." stringByAppendingString:args]
@interface TKControlButton()

//模仿button实现target-action
@property (nonatomic, assign)id target;
@property (nonatomic, assign)SEL action;

@property (nonatomic, strong) UIImageView *iconImageView;
@property (nonatomic, strong) NSString *imageName;
@property (nonatomic, strong) NSString *disableImageName;

@property (nonatomic, strong) UILabel *titleLabel;

@end
@implementation TKControlButton

- (instancetype)initWithFrame:(CGRect)frame imageName:(NSString *)imageName disableImageName:(NSString *)disableImageName title:(NSString *)title{
    
    if (self = [super initWithFrame:frame]) {
        
        CGFloat width = frame.size.width;
        CGFloat height = frame.size.height;
        CGFloat imageHeight = height/10.0*6.0;
        
        CGFloat titleHeight = height-imageHeight;
        self.imageName = imageName;
        self.disableImageName = disableImageName;
        
        self.enable = YES;
        
        _iconImageView = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, imageHeight, imageHeight)];
        _iconImageView.sakura.image(imageName);
        
        _iconImageView.contentMode = UIViewContentModeScaleAspectFit;
        [self addSubview:_iconImageView];
        
        
        _titleLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, imageHeight, width, titleHeight)];
        _titleLabel.text = title;
        
        CGFloat fontSize;
        if (width/4.0<titleHeight) {
            fontSize = width/4.0 - 1;
        }else{
            fontSize = titleHeight;
        }
        _titleLabel.font = [UIFont systemFontOfSize:fontSize];
        
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.sakura.textColor(ThemeKP(@"btnColor"));
        [self addSubview:_titleLabel];
        
    }
    return self;
}
- (void)controlAddTarget:(id)target action:(SEL)action{
    {
        _target = target;
        _action = action;
    }
}
//target相当于目标
//action相当于执行方法
//是否执行
-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)even
{
    //是否实现了这个方法  设计到内存泄露所以才报黄
    if (_enable) {
        
        [_target performSelector:_action withObject:self afterDelay:0];
    }
    
    
}
- (void)setEnable:(BOOL)enable{
    _enable = enable;
    if (enable ) {
        if (_iconImageView) {
            
            _iconImageView.sakura.image(_imageName);
        }
    }else{
        
        if (_iconImageView) {
          _iconImageView.sakura.image(_disableImageName);
        }
    }
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
