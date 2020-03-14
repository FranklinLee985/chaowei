//
//  TKCTFileListHeaderView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/16.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TKCTFileListHeaderViewDelegate <NSObject>
- (void)fileType:(TKFileType)type;
- (void)nameSort:(TKSortFileType)type;
- (void)typeSort:(TKSortFileType)type;
- (void)timeSort:(TKSortFileType)type;
@end

@interface TKCTFileListHeaderView : UIView
@property (nonatomic, weak)id<TKCTFileListHeaderViewDelegate> delegate;
@property (nonatomic, strong) void (^takePhotoActionBlock)();
@property (nonatomic, strong) void (^choosePhotoActionblock)();

- (instancetype)initWithFrame:(CGRect)frame fileType:(BOOL)type;

- (void)hideUploadButton:(BOOL)hide;

@end

