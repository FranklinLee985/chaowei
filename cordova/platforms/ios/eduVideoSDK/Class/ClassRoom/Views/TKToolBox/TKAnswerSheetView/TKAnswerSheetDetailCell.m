//
//  TKAnswerSheetDetailCell.m
//  EduClass
//
//  Created by maqihan on 2019/1/7.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetDetailCell.h"

@interface TKAnswerSheetDetailCell()

@end

@implementation TKAnswerSheetDetailCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        
        [self.contentView addSubview:self.serialLabel];
        [self.contentView addSubview:self.numLabel];
        [self.contentView addSubview:self.progressView];

        [self.serialLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.contentView.mas_left).offset(20);
            make.centerY.equalTo(self.contentView.mas_centerY);
            make.width.equalTo(@12);
        }];
        
        [self.progressView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.serialLabel.mas_right).offset(20);
            make.right.equalTo(self.contentView.mas_right).offset(-50);
            make.centerY.equalTo(self.contentView.mas_centerY);
            make.height.equalTo(@13);
        }];
        
        [self.numLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.contentView.mas_right).offset(-20);
            make.centerY.equalTo(self.contentView.mas_centerY);
        }];
    }
    return self;
}

- (UILabel *)numLabel
{
    if (!_numLabel) {
        _numLabel = [[UILabel alloc] init];
        _numLabel.textAlignment = NSTextAlignmentRight;
        _numLabel.font = [UIFont systemFontOfSize:12];
//        _numLabel.textColor = [TKHelperUtil colorWithHexColorString:@"A291D3"];
        _numLabel.sakura.textColor(@"TKToolsBox.answer_text");
        _numLabel.text = @"0人";
    }
    return _numLabel;
}

- (UILabel *)serialLabel
{
    if (!_serialLabel) {
        _serialLabel = [[UILabel alloc] init];
        _serialLabel.font = [UIFont systemFontOfSize:16];
//        _serialLabel.textColor = [TKHelperUtil colorWithHexColorString:@"A291D3"];
        _serialLabel.sakura.textColor(@"TKToolsBox.answer_text");
        _serialLabel.text = @"A";
    }
    return _serialLabel;
}

- (TKProgressView *)progressView
{
    if (!_progressView) {
        _progressView = [[TKProgressView alloc] init];
    }
    return _progressView;
}
@end
