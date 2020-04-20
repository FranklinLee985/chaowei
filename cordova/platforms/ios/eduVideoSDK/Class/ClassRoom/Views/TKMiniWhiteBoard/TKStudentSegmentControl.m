//
//  TKStudentsIndicator.m
//  TKWhiteBoard
//
//  Created by 周洁 on 2019/1/7.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//
//71 * 37   71 * 31
//
//默认添加了老师的画布

#import "TKStudentSegmentControl.h"
#import <TKRoomSDK/TKRoomSDK.h>

#define ThemeKP(args) [@"TKNativeWB.LightWB." stringByAppendingString:args]

@implementation TKStudentSegmentControl
{
    UIView *_landView;
    
    UIButton *_leftButton;
    UIButton *_rightButton;
    
    long _movementNum;//负数表示左移，不可能为正！
    
    TKStudentSegmentObject *_choosedStudent;
    
    int _displayNum;
    
    int _listLeft;
    int _listRight;
}

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

- (instancetype)initWithDelegate:(id<TKStudentSegmentControlDelegate>)delegate
{
    if (self = [super init]) {
        
        _buttons = [@[] mutableCopy];
        _students = [@[] mutableCopy];
        _movementNum = 0;
        _delegate = delegate;
        _displayNum = IS_PAD ? 6 : 2;
        
        _listLeft = 0;
        _listRight = 0;
        
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 10 * Proportion;
        
        _landView = [[UIView alloc] init];
        _landView.backgroundColor = UIColor.clearColor;
        _landView.clipsToBounds = YES;
        [self addSubview:_landView];
        
        [self createButtons];
        
        TKStudentSegmentObject *teacher = [TKStudentSegmentObject teacher];
        _choosedStudent = teacher;
        
        [self addStudent:teacher];
        
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
            self.userInteractionEnabled = NO;
        }
        
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
            self.userInteractionEnabled = YES;
        }
        
        
    }
    
    return self;
}

- (void)createButtons
{
    for (int i = 0; i < _displayNum + 1; i++) {
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.contentEdgeInsets = UIEdgeInsetsMake(0, 0, 10, 0);
        btn.layer.masksToBounds = YES;
        btn.layer.cornerRadius = 10 * Proportion;
        [btn addTarget:self action:@selector(didSelectStudent:) forControlEvents:UIControlEventTouchUpInside];
        [_landView addSubview:btn];
        
        [btn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(_buttons.lastObject ? _buttons.lastObject.mas_right : _landView.mas_left).offset(_buttons.lastObject ? 2 : 0);
            make.top.equalTo(_landView.mas_top).offset(_buttons.lastObject ? 6 * Proportion : 0);
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(71 * Proportion, (37 + 8) * Proportion)]).priorityHigh();
        }];

        _buttons.lastObject ? btn.sakura.backgroundColor(ThemeKP(@"tip_bg_nor_color")) :  btn.sakura.backgroundColor(ThemeKP(@"tip_bg_sel_color"));
        
        [_buttons addObject:btn];
        btn.hidden = YES;
    }
    
    [_landView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.mas_left).priorityHigh();
        make.top.equalTo(self.mas_top);
        make.bottom.equalTo(_buttons.firstObject.mas_bottom);
        make.right.equalTo(_buttons.lastObject.mas_right);
    }];
    
    _leftButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _leftButton.sakura.backgroundImage(ThemeKP(@"tk_left_default"), UIControlStateNormal);
    _leftButton.sakura.backgroundImage(ThemeKP(@"tk_left_disable"), UIControlStateDisabled);
    [_leftButton addTarget:self action:@selector(goLeft) forControlEvents:UIControlEventTouchUpInside];
    
    [self addSubview:_leftButton];
    
    _rightButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _rightButton.sakura.backgroundImage(ThemeKP(@"tk_right_default"), UIControlStateNormal);
    _rightButton.sakura.backgroundImage(ThemeKP(@"tk_right_disable"), UIControlStateDisabled);
    [_rightButton addTarget:self action:@selector(goRight) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_rightButton];
    
    [_leftButton mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(_buttons[_displayNum - 1].mas_right).offset(2);
        make.top.equalTo(self.mas_top).offset(6 * Proportion);
        make.height.equalTo(@(37 * Proportion));
        make.width.equalTo(@(45 * Proportion));
    }];
    
    [_rightButton mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(_leftButton.mas_right).offset(2);
        make.top.equalTo(self.mas_top).offset(6 * Proportion);
        make.height.equalTo(@(37 * Proportion));
        make.width.equalTo(@(45 * Proportion));
    }];
    
    _leftButton.hidden = _rightButton.hidden = YES;
}

- (BOOL)addStudent:(TKStudentSegmentObject *)student
{
    //避免重复添加
    for (TKStudentSegmentObject *obj in _students) {
        if ([obj.ID isEqualToString:student.ID]) {
            return NO;
        }
    }
    
    [_students addObject:student];
    //必须对student的seq进行排序
    [self sort];
    
    if (_students.count <= _displayNum + 1) {
        _listLeft = 0;
        _listRight = (int)_students.count;
    } else {
        _listRight = _listLeft + _displayNum;
    }
    
    [self updateUI];
    return YES;
}

- (void)updateUI
{
    [_buttons enumerateObjectsUsingBlock:^(UIButton * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        obj.hidden = YES;
    }];

    if (_students.count <= _displayNum + 1) {
        [_students enumerateObjectsUsingBlock:^(TKStudentSegmentObject * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([_choosedStudent.ID isEqualToString:obj.ID]) {
                _buttons[idx].sakura.backgroundColor(ThemeKP(@"tip_bg_sel_color"));
                [_buttons[idx] setAttributedTitle:[[NSAttributedString alloc] initWithString:obj.nickName attributes:@{NSFontAttributeName : [UIFont systemFontOfSize:14 * Proportion], NSForegroundColorAttributeName : [TXSakuraManager tx_colorWithPath:ThemeKP(@"tip_text_sel_color")]}] forState:UIControlStateNormal];
                _buttons[idx].hidden = NO;
                [_buttons[idx] mas_updateConstraints:^(MASConstraintMaker *make) {
                    make.top.equalTo(self.mas_top);
                }];
            } else {
                _buttons[idx].sakura.backgroundColor(ThemeKP(@"tip_bg_nor_color"));
                [_buttons[idx] setAttributedTitle:[[NSAttributedString alloc] initWithString:obj.nickName attributes:@{NSFontAttributeName : [UIFont systemFontOfSize:14 * Proportion], NSForegroundColorAttributeName : [TXSakuraManager tx_colorWithPath:ThemeKP(@"tip_text_nor_color")]}] forState:UIControlStateNormal];
                _buttons[idx].hidden = NO;
                [_buttons[idx] mas_updateConstraints:^(MASConstraintMaker *make) {
                    make.top.equalTo(self.mas_top).offset(6 * Proportion);
                }];
            }
        }];
        _leftButton.hidden = _rightButton.hidden = YES;
    } else {
        [[_students subarrayWithRange:NSMakeRange(_listLeft, _listRight - _listLeft)] enumerateObjectsUsingBlock:^(TKStudentSegmentObject * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            
            if ([_choosedStudent.ID isEqualToString:obj.ID]) {
                //选中
                _buttons[idx].sakura.backgroundColor(ThemeKP(@"tip_bg_sel_color"));
                [_buttons[idx] setAttributedTitle:[[NSAttributedString alloc] initWithString:obj.nickName attributes:@{NSFontAttributeName : [UIFont systemFontOfSize:14 * Proportion], NSForegroundColorAttributeName : [TXSakuraManager tx_colorWithPath:ThemeKP(@"tip_text_sel_color")]}] forState:UIControlStateNormal];
                _buttons[idx].hidden = NO;
                [_buttons[idx] mas_updateConstraints:^(MASConstraintMaker *make) {
                    make.top.equalTo(self.mas_top);
                }];
                
            } else {
                _buttons[idx].sakura.backgroundColor(ThemeKP(@"tip_bg_nor_color"));
                [_buttons[idx] setAttributedTitle:[[NSAttributedString alloc] initWithString:obj.nickName attributes:@{NSFontAttributeName : [UIFont systemFontOfSize:14 * Proportion], NSForegroundColorAttributeName : [TXSakuraManager tx_colorWithPath:ThemeKP(@"tip_text_nor_color")]}] forState:UIControlStateNormal];
                _buttons[idx].hidden = NO;
                [_buttons[idx] mas_updateConstraints:^(MASConstraintMaker *make) {
                    make.top.equalTo(self.mas_top).offset(6 * Proportion);
                }];
            }
        }];
        _leftButton.hidden = _rightButton.hidden = NO;
    }
}

- (void)sort
{
    if (_students.count < 2) {
        return;
    }
    
    //从老师后面开始排序
    NSArray *sortedArray = [[_students subarrayWithRange:NSMakeRange(1, _students.count - 1)] sortedArrayUsingComparator:^NSComparisonResult(TKStudentSegmentObject  * _Nonnull obj1, TKStudentSegmentObject  * _Nonnull obj2) {
        return ([obj1.ID compare:obj2.ID options:NSDiacriticInsensitiveSearch]);
    }];
    [_students replaceObjectsInRange:NSMakeRange(1, _students.count - 1) withObjectsFromArray:sortedArray];
}

- (void)removeStudent:(TKStudentSegmentObject *)student
{
    int index = 0;
    for (TKStudentSegmentObject *obj in _students) {
        if ([student.ID isEqualToString:obj.ID]) {
            [_students removeObject:obj];
            break;
        }
        index++;
    }
    
    if (index == _students.count + 1) {
        //没找到需要删除的student
        return;
    }
    
    
    if (_students.count <= _displayNum + 1) {
        _listLeft = 0;
        _listRight = (int)_students.count;
    } else {
        if (_listRight > _students.count) {
            _listRight = (int)_students.count;
            _listLeft = _listRight - _displayNum;
        }
    }
    
    [self updateUI];
}

- (void)didSelectStudent:(UIButton *)btn
{
    [self chooseStudent:_students[[_buttons indexOfObject:btn] + _listLeft]];
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(didSelectStudent:)]) {
        [self.delegate didSelectStudent:_students[[_buttons indexOfObject:btn] + _listLeft]];
    }
}


- (void)chooseStudent:(TKStudentSegmentObject *)student;
{
//    涉及到翻页，还未处理
    __block NSUInteger index = 0;
    [_students enumerateObjectsUsingBlock:^(TKStudentSegmentObject * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj.ID isEqualToString:student.ID]) {
            index = idx;
        }
    }];
    
    if (index >= _listLeft && index < _listRight) {
        //视野内
        _choosedStudent = student;
        [self updateUI];
        return;
    }
    
    int onPageNum = (int)(index / _displayNum);
    
    _listLeft = onPageNum * _displayNum;
    if (_students.count - _listLeft >= _displayNum) {
        _listRight = (onPageNum + 1) * _displayNum;
    } else {
        int delta = ((int)_students.count - onPageNum * _displayNum);
        if (onPageNum > 0) {
            _listLeft -= (_displayNum - delta);
            _listRight = _listLeft + _displayNum;
        } else {
            _listRight = delta;
        }
    }
    
    _choosedStudent = student;
    [self updateUI];
}

- (void)goRight
{
    int step = 0;
    while (1) {
        if (step == _displayNum) {
            break;
        }
        //步数达到之前++
        _listRight++;
        step++;
        
        //已越界count，--
        if (_listRight > _students.count) {
            _listRight--;
            step--;
            break;
        }
    }
    _listLeft += step;
    
    if (step == 0) {
        return;
    }
    
    [self updateUI];
}

- (void)goLeft
{
    int step = 0;
    while (1) {
        if (step == _displayNum) {
            break;
        }
        if (--_listLeft < 0) {
            _listLeft++;
            break;
        } else {
            step++;
            _listRight--;
        }
    }
    
    [self updateUI];
    
}

- (void)resetUI
{
    _leftButton.hidden = _rightButton.hidden = YES;
    [_students removeAllObjects];
    [_students addObject:[TKStudentSegmentObject teacher]];
    
    _listLeft = 0;
    _listRight = 1;
    [_buttons enumerateObjectsUsingBlock:^(UIButton * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        obj.hidden = YES;
    }];
}

@end
