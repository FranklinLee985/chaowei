//
//  TKAnswerSheetSetupCell.m
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetSetupCell.h"
@interface TKAnswerSheetSetupCell()

@end

@implementation TKAnswerSheetSetupCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        
        [self.contentView addSubview:self.itemImage];
        
        [self.itemImage mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.contentView);
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
@end
