//
//  TKAnswerSheetRecordCell.m
//  EduClass
//
//  Created by maqihan on 2019/1/7.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetRecordCell.h"
@interface TKAnswerSheetRecordCell ()

@property (strong , nonatomic) UIView *containerView;

@end

@implementation TKAnswerSheetRecordCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        
        [self.contentView addSubview:self.containerView];

        [self.containerView addSubview:self.nickname];
        [self.containerView addSubview:self.answerLabel];
        [self.containerView addSubview:self.timeLabel];
        
        [self.containerView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.contentView.mas_left).offset(10);
            make.top.equalTo(self.contentView.mas_top);
            make.right.equalTo(self.contentView.mas_right).offset(-10);
            make.bottom.equalTo(self.contentView.mas_bottom).offset(-5);
        }];
        
        [self.nickname mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.containerView.mas_left).offset(3);
            make.right.equalTo(self.answerLabel.mas_left).offset(-5);
            make.centerY.equalTo(self.containerView.mas_centerY);
        }];
        
        [self.answerLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.containerView.mas_centerX).offset(-20);
            make.centerY.equalTo(self.containerView.mas_centerY);
        }];
        
        [self.timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.containerView.mas_right).offset(-3);
            make.centerY.equalTo(self.containerView.mas_centerY);
        }];
    }
    return self;
}


- (UIView *)containerView
{
    if (!_containerView) {
        _containerView = [[UIView alloc] init];
        _containerView.sakura.backgroundColor(@"TKToolsBox.answer_trackView");
        _containerView.layer.cornerRadius = 8;
    }
    return _containerView;
}

- (UILabel *)nickname
{
    if (!_nickname) {
        _nickname = [[UILabel alloc] init];
        _nickname.textAlignment = NSTextAlignmentLeft;
        if (IS_IPHONE) {
            _nickname.font = [UIFont systemFontOfSize:10];
        }else{
            _nickname.font = [UIFont systemFontOfSize:13];
        }
//        _nickname.textColor = [TKHelperUtil colorWithHexColorString:@"A291D3"];
        _nickname.sakura.textColor(@"TKToolsBox.answer_text_1");
        _nickname.text = @"";
    }
    return _nickname;
}

- (UILabel *)answerLabel
{
    if (!_answerLabel) {
        _answerLabel = [[UILabel alloc] init];
        if (IS_IPHONE) {
            _answerLabel.font = [UIFont systemFontOfSize:10];
        }else{
            _answerLabel.font = [UIFont systemFontOfSize:13];
        }
        //        _answerLabel.textColor = [TKHelperUtil colorWithHexColorString:@"A291D3"];
        _answerLabel.sakura.textColor(@"TKToolsBox.answer_text_1");

        _answerLabel.text = @"";
    }
    return _answerLabel;
}

- (UILabel *)timeLabel
{
    if (!_timeLabel) {
        _timeLabel = [[UILabel alloc] init];
        _timeLabel.textAlignment = NSTextAlignmentRight;
        if (IS_IPHONE) {
            _timeLabel.font = [UIFont systemFontOfSize:10];
        }else{
            _timeLabel.font = [UIFont systemFontOfSize:13];
        }
        //        _timeLabel.textColor = [TKHelperUtil colorWithHexColorString:@"A291D3"];
        _timeLabel.sakura.textColor(@"TKToolsBox.answer_text_1");

        _timeLabel.text = @"";
    }
    return _timeLabel;
}

@end
