//
//  TKCTUploadView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/19.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTUploadView.h"

#define ThemeKP(args) [@"ClassRoom.TKUploadImageView." stringByAppendingString:args]
#define tWidth (IS_PAD ? 201 : 201 / 3.0f * 2)
#define tHeight (IS_PAD ? 110 : 110 / 3.0f * 2)
#define tCornerH (IS_PAD ? 10 : 10 / 3.0f * 2)
//#define tWidth 201
//#define tHeight 110
//#define tCornerH 10
@interface TKCTUploadView()

@property (nonatomic, strong) UIView       * backView;

@property (nonatomic, strong) UIImageView *contentView;
@property (nonatomic, strong) UIButton *cameraBtn;
@property (nonatomic, strong) UIButton *picBtn;
@end

@implementation TKCTUploadView
- (id)initWithFrame:(CGRect)frame
{
    if (self == [super initWithFrame:frame])
    {
        
        
        if (!self.backView) {
            self.backView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, ScreenW, ScreenH)];
            self.backView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.1];
            self.backView.alpha = 0;
            UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget: self action: @selector(touchOutSide)];
            [self.backView addGestureRecognizer: tap];
            
        }
        
        [self initContent];
    }
    
    return self;
}
- (void)touchOutSide{
    if (self.dismiss) {
        self.dismiss();
    }
    [self dissMissView];
}

- (void)initContent
{
    //alpha 0.0  白色   alpha 1 ：黑色   alpha 0～1 ：遮罩颜色，逐渐
    self.backgroundColor = [UIColor clearColor];
    self.userInteractionEnabled = YES;
    
    self.contentView = ({

        UIImageView *view = [[UIImageView alloc]initWithFrame:CGRectMake(0, 5, tWidth, tHeight)];
        [self addSubview:view];
        view.image = [UIImage tkResizedImageWithName:ThemeKP(@"camera_tool_toolbar")];
        view.userInteractionEnabled = YES;
        view;


    });
    
    
    float buttonWidth = tWidth / 4.0f;
    
    
    self.cameraBtn = ({
        UIButton *button = [[UIButton alloc]initWithFrame:CGRectMake(0, tCornerH, buttonWidth, buttonWidth)];
        button.sakura.backgroundImage(ThemeKP(@"choose_camera_pop"),UIControlStateNormal);
        button.imageView.contentMode = UIViewContentModeCenter;
        [self.contentView addSubview:button];
        [button addTarget:self action:@selector(cameraBtnClick:) forControlEvents:(UIControlEventTouchUpInside)]; 
        button.centerX = tWidth / 4.0f;
        button.centerY = tHeight / 3.0f + 6;
        button.layer.masksToBounds = YES;
        button.layer.cornerRadius = buttonWidth / 2;
        button;
    });
    
    self.picBtn = ({
        UIButton *button = [[UIButton alloc]initWithFrame:CGRectMake(0, CGRectGetMaxY(self.cameraBtn.frame), buttonWidth, buttonWidth)];
        button.sakura.backgroundImage(ThemeKP(@"choose_photo_pop"),UIControlStateNormal);
        button.imageView.contentMode = UIViewContentModeCenter;
        [self.contentView addSubview:button];
        [button addTarget:self action:@selector(picBtnClick:) forControlEvents:(UIControlEventTouchUpInside)];
        button.centerX = tWidth / 4.0f * 3;
        button.centerY = tHeight / 3.0f + 6;
        button.layer.masksToBounds = YES;
        button.layer.cornerRadius = buttonWidth / 2;
        button;
    });
    
    UILabel *cameraLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, tWidth / 2.0f, 20)];
    cameraLabel.text = TKMTLocalized(@"UploadPhoto.TakePhoto");
    cameraLabel.textColor = UIColor.whiteColor;
    cameraLabel.font = TKFont(13);
    cameraLabel.textAlignment = NSTextAlignmentCenter;
    cameraLabel.centerX = self.cameraBtn.centerX;
    cameraLabel.centerY = (tHeight - CGRectGetMaxY(self.cameraBtn.frame)) / 2.0f + CGRectGetMaxY(self.cameraBtn.frame);
    [self.contentView addSubview:cameraLabel];
    
    UILabel *galleryLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, tWidth / 2.0f, 20)];
    galleryLabel.text = TKMTLocalized(@"UploadPhoto.FromGallery");
    galleryLabel.textColor = UIColor.whiteColor;
    galleryLabel.font = TKFont(13);
    galleryLabel.textAlignment = NSTextAlignmentCenter;
    galleryLabel.centerX = self.picBtn.centerX;
    galleryLabel.centerY = (tHeight - CGRectGetMaxY(self.picBtn.frame)) / 2.0f + CGRectGetMaxY(self.picBtn.frame);
    [self.contentView addSubview:galleryLabel];

}
- (void)cameraBtnClick:(UIButton *)sender{
    
    if (self.dismiss) {
        self.dismiss();
    }
    [TKEduSessionHandle shareInstance].updateImageUseType = TKUpdateImageUseType_Document;
    [[NSNotificationCenter defaultCenter] postNotificationName:sTakePhotosUploadNotification object:sTakePhotosUploadNotification];
    [self dissMissView];
    
}
- (void)picBtnClick:(UIButton *)sender{
    if (self.dismiss) {
        self.dismiss();
    }
    [TKEduSessionHandle shareInstance].updateImageUseType = TKUpdateImageUseType_Document;
    [[NSNotificationCenter defaultCenter] postNotificationName:sChoosePhotosUploadNotification object:sChoosePhotosUploadNotification];
    [self dissMissView];
}
//展示从底部向上弹出的UIView（包含遮罩）
- (void)showOnView:(UIButton *)view
{
    CGRect absoluteRect = [view convertRect:view.bounds toView:[UIApplication sharedApplication].keyWindow];
    
    CGFloat x = (tWidth - absoluteRect.size.width)/2.0;
    
    [TKMainWindow addSubview:self.backView];
    [TKMainWindow addSubview:self];
    
    
    self.alpha = 1.0;
    self.frame = CGRectMake(absoluteRect.origin.x-x, absoluteRect.origin.y + absoluteRect.size.height, tWidth, tHeight);
    [UIView animateWithDuration: 0.25 animations:^{
        
        self.layer.affineTransform = CGAffineTransformMakeScale(1.0, 1.0);
        self.alpha = 1;
        self.backView.alpha = 1;
    } completion:^(BOOL finished) {
        
    }];
}


//移除从上向底部弹下去的UIView（包含遮罩）
- (void)dissMissView
{
    
    [UIView animateWithDuration:0.3f
                     animations:^{
                         
                         self.alpha = 0.0;
                         
                         self.backView.alpha = 0;
                         
                     }
                     completion:^(BOOL finished){
                         
                         [self.backView removeFromSuperview];
                         [self removeFromSuperview];
                         
                     }];
    
}

@end
