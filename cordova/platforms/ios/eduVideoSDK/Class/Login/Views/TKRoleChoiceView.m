//
//  TKRoleChoiceView.m
//  EduClass
//
//  Created by lyy on 2018/4/28.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKRoleChoiceView.h"

@interface TKRoleChoiceView()

@property (strong, nonatomic)  UILabel *titleLabel;
@property (strong, nonatomic)  UIButton *cancelButton;//取消
@property (strong, nonatomic)  UIView *lineView;
@property (strong, nonatomic)  UIButton *studentButton;
@property (strong, nonatomic)  UIButton *teacherButton;
@property (strong, nonatomic)  UIButton *patrolButton;

@end

@implementation TKRoleChoiceView
- (instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [TKHelperUtil colorWithHexColorString:@"F8F8FA"];
        
        _titleLabel = [[UILabel alloc]init];
        [self addSubview:_titleLabel];
        
        _cancelButton = [UIButton buttonWithType:(UIButtonTypeCustom)];
        [self addSubview:_cancelButton];
        
        _lineView = [[UIView alloc]init];
        [self addSubview:_lineView];
        
        _studentButton = [UIButton buttonWithType:(UIButtonTypeCustom)];
        [self addSubview:_studentButton];
        
        _teacherButton = [UIButton buttonWithType:(UIButtonTypeCustom)];
        [self addSubview:_teacherButton];
        
        _patrolButton =[UIButton buttonWithType:(UIButtonTypeCustom)];
        [self addSubview:_patrolButton];
        
        
        [self loadAllView];
        [self showDefault];
        
    }
    return self;
}

- (void)loadAllView{
    _titleLabel.frame = CGRectMake(0, 0, self.width, self.height/5.0);
    _titleLabel.textAlignment = NSTextAlignmentCenter;
    
    _cancelButton.frame = CGRectMake(self.width-65, 0, 60, self.height/5.0);
    _cancelButton.titleLabel.textAlignment = NSTextAlignmentRight;
    
    [_cancelButton addTarget:self action:@selector(cancelButtonClick:) forControlEvents:(UIControlEventTouchUpInside)];
    
    
    _lineView.frame = CGRectMake(0, CGRectGetMaxY(_titleLabel.frame), self.width, 1);
    
    CGFloat btnWidth = 100;
    CGFloat btnHeight = 36;
    
    CGFloat btnY = (self.height - CGRectGetMaxY(_lineView.frame)-btnHeight)/2.0+CGRectGetMaxY(_lineView.frame);
    
    
    CGFloat margin = (self.width-(btnWidth * 3))/4.0;
    
    _studentButton.frame = CGRectMake(margin, btnY, btnWidth, btnHeight);
    
    _teacherButton.frame = CGRectMake(_studentButton.rightX+margin, btnY, btnWidth, btnHeight);
    
    _patrolButton.frame = CGRectMake(_teacherButton.rightX+margin, btnY, btnWidth, btnHeight);
    
    _titleLabel.text = TKMTLocalized(@"Label.choiceIdentity");
    
    _titleLabel.textColor = [TKHelperUtil colorWithHexColorString:@"222222"];
    
    [_cancelButton setTitle:TKMTLocalized(@"Prompt.Cancel") forState:UIControlStateNormal];
    
    [_cancelButton setTitleColor:[TKHelperUtil colorWithHexColorString:@"222222"] forState:UIControlStateNormal];
    
    _lineView.backgroundColor = [TKHelperUtil colorWithHexColorString:@"C7C7DF"];
    
    
    [self resetButton:_studentButton title:TKMTLocalized(@"Role.Student") action:@selector(studentClick:)];
    
    [self resetButton:_teacherButton title:TKMTLocalized(@"Role.Teacher") action:@selector(teacherClick:)];
    
    [self resetButton:_patrolButton title:TKMTLocalized(@"Role.Patrol") action:@selector(assistantClick:)];
    
    
    
}
- (void)showDefault{
    
    NSNumber  *role = [[NSUserDefaults standardUserDefaults] objectForKey:@"userrole"];
    
    NSString *roleStr;
    if (role != nil && [role isKindOfClass:[NSNumber class]])
    {
        switch ([role intValue]) {
            case 0:
                roleStr = TKMTLocalized(@"Role.Teacher");
                break;
            case 2:
                roleStr = TKMTLocalized(@"Role.Student");
                break;
            case 4:
                roleStr = TKMTLocalized(@"Role.Patrol");
            default:
                break;
        }
    }else{
        
        roleStr = TKMTLocalized(@"Role.Student");
    }
    
    
    if ([roleStr isEqualToString:_teacherButton.titleLabel.text])
    {
        _teacherButton.selected = YES;
        _teacherButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"0077FF"];
        
    }else if ([roleStr isEqualToString:_patrolButton.titleLabel.text]){
        
        _patrolButton.selected = YES;
        _patrolButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"0077FF"];
        
    }else{
        _studentButton.selected = YES;
        _studentButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"0077FF"];
    }
}
- (void)resetButton:(UIButton *)button title:(NSString *)title action:(SEL)action{
    
    [button setTitle:title forState:UIControlStateNormal];
    [button setTitleColor:[TKHelperUtil colorWithHexColorString:@"0077FF"] forState:UIControlStateNormal];
    [button setTitleColor:[TKHelperUtil colorWithHexColorString:@"ffffff"] forState:UIControlStateSelected];

    button.layer.masksToBounds = YES;
    button.layer.cornerRadius = 18;
    button.layer.borderColor = [TKHelperUtil colorWithHexColorString:@"0077FF"].CGColor;
    button.layer.borderWidth = 1;
    [button addTarget:self action:action forControlEvents:(UIControlEventTouchUpInside)];
    
}

- (void)cancelButtonClick:(UIButton *)sender{
    if (self.delegate && [self.delegate respondsToSelector:@selector(choiceCancel)]) {
        [self.delegate choiceCancel];
    }
}
- (void)studentClick:(UIButton *)sender{
    
    self.studentButton.selected = YES;
    self.teacherButton.selected = NO;
    self.patrolButton.selected = NO;
    
    self.studentButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"0077FF"];
    self.teacherButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"ffffff"];
    self.patrolButton.backgroundColor  = [TKHelperUtil colorWithHexColorString:@"ffffff"];

    
    if (self.delegate && [self.delegate respondsToSelector:@selector(choiceRole:)]) {
        [self.delegate choiceRole:TKMTLocalized(@"Role.Student")];
    }
}
- (void)teacherClick:(UIButton *)sender{
    
    self.studentButton.selected = NO;
    self.teacherButton.selected = YES;
    self.patrolButton.selected = NO;
    
    self.teacherButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"0077FF"];
    self.studentButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"ffffff"];
    self.patrolButton.backgroundColor  = [TKHelperUtil colorWithHexColorString:@"ffffff"];

    
    if (self.delegate && [self.delegate respondsToSelector:@selector(choiceRole:)]) {
        [self.delegate choiceRole:TKMTLocalized(@"Role.Teacher")];
    }
}
- (void)assistantClick:(UIButton *)sender{
    
    self.studentButton.selected = NO;
    self.teacherButton.selected = NO;
    self.patrolButton.selected = YES;

    self.patrolButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"0077FF"];
    self.studentButton.backgroundColor = [TKHelperUtil colorWithHexColorString:@"ffffff"];
    self.teacherButton.backgroundColor  = [TKHelperUtil colorWithHexColorString:@"ffffff"];

    if (self.delegate && [self.delegate respondsToSelector:@selector(choiceRole:)]) {
        [self.delegate choiceRole:TKMTLocalized(@"Role.Patrol")];
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
