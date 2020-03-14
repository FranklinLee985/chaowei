//
//  TKPopViewCell.m
//  EduClass
//
//  Created by maqihan on 2019/1/3.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKPopViewCell.h"
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKPopViewCell()

@end

@implementation TKPopViewCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];

        [self.contentView addSubview:self.itemImage];
        [self.contentView addSubview:self.itemTitle];
        
        [self.itemImage mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.contentView.mas_centerY).offset(-10);
            make.centerX.equalTo(self.contentView.mas_centerX);
            make.width.equalTo(@Fit(50));
            make.height.equalTo(@Fit(50));
        }];
        
        [self.itemTitle mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.contentView.mas_left);
            make.right.equalTo(self.contentView.mas_right);
            make.top.equalTo(self.itemImage.mas_bottom).offset(10);
        }];
    }
    return self;
}

- (UIImageView *)itemImage
{
    if (!_itemImage) {
        _itemImage = [[UIImageView alloc] init];
        _itemImage.contentMode = UIViewContentModeScaleAspectFit;
    }
    return _itemImage;
}

- (UILabel *)itemTitle
{
    if (!_itemTitle) {
        _itemTitle = [[UILabel alloc] init];
        _itemTitle.font = [UIFont systemFontOfSize:Fit(12)];
        _itemTitle.textColor = [UIColor whiteColor];
        _itemTitle.textAlignment = NSTextAlignmentCenter;
    }
    return _itemTitle;
}

@end
