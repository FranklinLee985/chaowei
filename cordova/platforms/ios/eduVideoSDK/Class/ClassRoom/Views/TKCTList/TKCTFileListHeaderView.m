//
//  TKCTFileListHeaderView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/16.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTFileListHeaderView.h"
#import "TKEduSessionHandle.h"
#import "TKPageButton.h"

#define kMargin 10
#define btnMargin 5
#define ThemeKP(args) [@"TKDocumentListView." stringByAppendingString:args]

@interface TKCTFileListHeaderView() {
    CGFloat _typeViewWidth;
    CGFloat _btnWidth;
    CGFloat _toolHeight;
    
    UIView  *_takePhotoView;
    UIView  *_choosePhotoView;
}

@property (nonatomic, strong) UIView         *typeView;
@property (nonatomic, strong) UIButton       *classFileButton;    //教室文件
@property (nonatomic, strong) UIButton       *systemFileButton;   //公用文件
@property (nonatomic, strong) TKPageButton   *timeSortBtn;        //时间排序
@property (nonatomic, strong) TKPageButton   *typeSortBtn;        //类型排序
@property (nonatomic, strong) TKPageButton   *nameSortBtn;        //时间排序
@property (nonatomic, strong) NSMutableArray *btnArray;           //按钮数组

@end

@implementation TKCTFileListHeaderView

- (instancetype)initWithFrame:(CGRect)frame fileType:(BOOL)type{
    if (self = [super initWithFrame:frame]) {
        _typeViewWidth = CGRectGetWidth(frame)/2.0 - kMargin*2;
        _btnWidth      = (CGRectGetWidth(frame)/2.0*0.8 - 15)/3.0;
        _toolHeight    = CGRectGetHeight(frame)/6.0*4;
        
        _typeView = ({
            UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0,
                                                                    CGRectGetHeight(frame)/6.0,
                                                                    _typeViewWidth,
                                                                    _toolHeight)];
            [self addSubview:view];
            view.layer.borderWidth = 1;
            view.layer.borderColor = [TKTheme cgColorWithPath:ThemeKP(@"listTypeBorderColor")];
            view.layer.masksToBounds = YES;
            view.layer.cornerRadius = CGRectGetHeight(view.frame)/2.0;
            view;
        });
        
        _classFileButton = ({
            UIButton *button = [UIButton buttonWithType:(UIButtonTypeCustom)];
            [_typeView addSubview:button];
            button.frame = CGRectMake(2,
                                      2,
                                      (_typeView.width - 4) / 2.0,
                                      (_typeView.height- 4));
            button.sakura.backgroundColor(ThemeKP(@"listFileColor"));
            [button setTitle:TKMTLocalized(@"Title.ClassroomDocuments") forState:UIControlStateNormal];
            button.sakura.titleColor(ThemeKP(@"listFileSelectColor"),UIControlStateNormal);
            [self setTheHalfAngle:button rectCorner:UIRectCornerTopLeft|UIRectCornerBottomLeft cornerRadii:CGRectGetHeight(button.frame)/2.0];
            button.layer.masksToBounds = YES;
            button.layer.cornerRadius = CGRectGetHeight(button.frame)/2.0;
            [button addTarget:self action:@selector(classFileButtonClick) forControlEvents:(UIControlEventTouchUpInside)];
            button;
        });
        
        
        _systemFileButton = ({
            UIButton *button = [UIButton buttonWithType:(UIButtonTypeCustom)];
            [_typeView addSubview:button];
            button.frame = CGRectMake(CGRectGetMaxX(_classFileButton.frame), 2, (CGRectGetWidth(_typeView.frame)-4)/2.0, (CGRectGetHeight(_typeView.frame)-4));
            button.backgroundColor = [UIColor clearColor];
            [button setTitle:TKMTLocalized(@"Title.PublicDocuments") forState:UIControlStateNormal];
            button.sakura.titleColor(ThemeKP(@"listFileDefaultColor"),UIControlStateNormal);
            [self setTheHalfAngle:button rectCorner:UIRectCornerTopRight|UIRectCornerBottomRight cornerRadii:CGRectGetHeight(button.frame)/2.0];
            button.layer.masksToBounds = YES;
            button.layer.cornerRadius = CGRectGetHeight(button.frame)/2.0;
            [button addTarget:self action:@selector(systemFileButtonClick) forControlEvents:(UIControlEventTouchUpInside)];
            button;
        });
        
        
        
        
        _nameSortBtn = ({
            TKPageButton *button = [TKPageButton buttonWithType:(UIButtonTypeCustom)];
            button.frame = CGRectMake(self.width-_btnWidth-btnMargin, 0, _btnWidth, _toolHeight);
            button.centerY = _typeView.centerY;
            button.sakura.titleColor(ThemeKP(@"listSortColor"),UIControlStateNormal);
            [button setTitle:TKMTLocalized(@"Button.nameSort") forState:(UIControlStateNormal)];
            button.titleLabel.font = _classFileButton.titleLabel.font;
            button.sakura.image(ThemeKP(@"arrange_none"),UIControlStateNormal);
            [self addSubview:button];
            button.tag = TKSortNone;
            [button addTarget:self action:@selector(nameSortBtnClick:) forControlEvents:(UIControlEventTouchUpInside)];
            button;
        });
        
        _typeSortBtn = ({
            
            TKPageButton *button = [TKPageButton buttonWithType:(UIButtonTypeCustom)];
            button.frame = CGRectMake(CGRectGetMinX(_nameSortBtn.frame)-_btnWidth-btnMargin,0, _btnWidth, _toolHeight);
            button.centerY = _nameSortBtn.centerY;
            button.sakura.titleColor(ThemeKP(@"listSortColor"),UIControlStateNormal);
            [button setTitle:TKMTLocalized(@"Button.typeSort") forState:(UIControlStateNormal)];
            button.titleLabel.font = _classFileButton.titleLabel.font;
            button.sakura.image(ThemeKP(@"arrange_none"),UIControlStateNormal);
            [button addTarget:self action:@selector(typeSortBtnClick:) forControlEvents:(UIControlEventTouchUpInside)];
            [self addSubview:button];
            button.tag = TKSortNone;
            button;
            
        });
        
        _timeSortBtn = ({
            
            TKPageButton *button = [TKPageButton buttonWithType:(UIButtonTypeCustom)];
            button.frame = CGRectMake(CGRectGetMinX(_typeSortBtn.frame)-_btnWidth-btnMargin, _nameSortBtn.y, _btnWidth, _toolHeight);
            button.centerY = _nameSortBtn.centerY;
            button.sakura.titleColor(ThemeKP(@"listSortColor"),UIControlStateNormal);
            [button setTitle:TKMTLocalized(@"Button.timeSort") forState:(UIControlStateNormal)];
            button.titleLabel.font = _classFileButton.titleLabel.font;
//            button.sakura.image(ThemeKP(@"arrange_up"),UIControlStateNormal);
            button.sakura.image(ThemeKP(@"arrange_down"),UIControlStateNormal);
            [button addTarget:self action:@selector(timeSortBtnClick:) forControlEvents:(UIControlEventTouchUpInside)];
            [self addSubview:button];
//            button.tag = TKSortAscending;
            button.tag = TKSortDescending;
            button;
        });
        _btnArray = [NSMutableArray arrayWithObjects:_nameSortBtn,_typeSortBtn,_timeSortBtn, nil];
        
        if (type) { //如果开启了文档分类配置项 需要不进行隐藏
            _typeView.hidden = NO;
        }else{
            _typeView.hidden = YES;
        }
        
        [self newUI];
    }
    return self;
}

- (void)newUI
{
    _typeViewWidth = CGRectGetWidth(self.frame)/2.0 - kMargin*2;
    _btnWidth      = (CGRectGetWidth(self.frame)/2.0*0.8 - 15)/3.0;
    _toolHeight    = CGRectGetHeight(self.frame)/6.0*4;
    
    _timeSortBtn.frame = CGRectMake(10, 10, _btnWidth, _toolHeight);
    _typeSortBtn.frame = CGRectMake(CGRectGetMaxX(_timeSortBtn.frame) + btnMargin, 10, _btnWidth, _toolHeight);
    _nameSortBtn.frame = CGRectMake(CGRectGetMaxX(_typeSortBtn.frame) + btnMargin, 10, _btnWidth, _toolHeight);
    _timeSortBtn.titleLabel.font = _typeSortBtn.titleLabel.font = _nameSortBtn.titleLabel.font = TKFont(12);
    _timeSortBtn.sakura.titleColor(@"TKUserListTableView.coursewareButtonYellowColor",UIControlStateNormal);
    _typeSortBtn.sakura.titleColor(@"TKUserListTableView.coursewareButtonWhiteColor",UIControlStateNormal);
    _nameSortBtn.sakura.titleColor(@"TKUserListTableView.coursewareButtonWhiteColor",UIControlStateNormal);
    
    _classFileButton.hidden     = YES;
    _systemFileButton.hidden    = YES;
    _typeView.hidden            = YES;
    
    _takePhotoView = [[UIView alloc] initWithFrame:CGRectMake(self.width - 79 - 20 - 79 - 20, 10, 79, _toolHeight)];
    _takePhotoView.backgroundColor = UIColor.clearColor;
    _takePhotoView.layer.cornerRadius = 5;
    _takePhotoView.layer.masksToBounds = YES;
    _takePhotoView.layer.borderWidth = 1;
    _takePhotoView.layer.sakura.borderColor(@"TKUserListTableView.borderColor");
    [self addSubview:_takePhotoView];
    
    UILabel *takePhotoLabel = [[UILabel alloc] initWithFrame:_takePhotoView.bounds];
    takePhotoLabel.sakura.textColor(@"TKUserListTableView.borderColor");
    takePhotoLabel.text = TKMTLocalized(@"UploadPhoto.TakePhoto");
    takePhotoLabel.textAlignment = NSTextAlignmentCenter;
    takePhotoLabel.font = TKFont(12);
    [_takePhotoView addSubview:takePhotoLabel];
    
    UITapGestureRecognizer *takephotoG = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(takePhoto)];
    [_takePhotoView addGestureRecognizer:takephotoG];
    
    
    _choosePhotoView = [[UIView alloc] initWithFrame:CGRectMake(self.width - 79 - 20, 10, 79, _toolHeight)];
    _choosePhotoView.backgroundColor = UIColor.clearColor;
    _choosePhotoView.layer.cornerRadius = 5;
    _choosePhotoView.layer.masksToBounds = YES;
    _choosePhotoView.layer.borderWidth = 1;
    _choosePhotoView.layer.sakura.borderColor(@"TKUserListTableView.borderColor");
    [self addSubview:_choosePhotoView];
    
    UILabel *choosePhotoLabel = [[UILabel alloc] initWithFrame:_choosePhotoView.bounds];
    choosePhotoLabel.sakura.textColor(@"TKUserListTableView.borderColor");
    choosePhotoLabel.text = TKMTLocalized(@"UploadPhoto.FromGallery");
    choosePhotoLabel.textAlignment = NSTextAlignmentCenter;
    choosePhotoLabel.font = TKFont(12);
    [_choosePhotoView addSubview:choosePhotoLabel];
    
    UITapGestureRecognizer *choosePhotoG = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(choosePhoto)];
    [_choosePhotoView addGestureRecognizer:choosePhotoG];
    
    _timeSortBtn.centerY = self.centerY;
    _typeSortBtn.centerY = self.centerY;
    _nameSortBtn.centerY = self.centerY;
    _takePhotoView.centerY = self.centerY;
    _choosePhotoView.centerY = self.centerY;
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol ) {
        _takePhotoView.hidden = YES;
        _choosePhotoView.hidden = YES;
    }
}

- (void)takePhoto
{
    if (self.takePhotoActionBlock) {
        self.takePhotoActionBlock();
    }
}

- (void)choosePhoto
{
    if (self.choosePhotoActionblock) {
        self.choosePhotoActionblock();
    }
}

- (void)hideUploadButton:(BOOL)hide
{
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        _choosePhotoView.hidden = YES;
        _takePhotoView.hidden = YES;
    } else {
        _choosePhotoView.hidden = hide;
        _takePhotoView.hidden = hide;
    }
}

- (void)setTheHalfAngle:(UIButton *)button rectCorner:(UIRectCorner)rectCorner cornerRadii:(CGFloat)radii{
    
    button.titleLabel.font = IS_PAD ? TKFont(15) : [UIFont systemFontOfSize:button.height/3.0*2.0];
    UIBezierPath *maskPath=[UIBezierPath bezierPathWithRoundedRect:button.bounds byRoundingCorners:rectCorner cornerRadii:CGSizeMake(radii, radii)];
    CAShapeLayer *maskLayer=[[CAShapeLayer alloc]init];
    maskLayer.frame=button.bounds;
    maskLayer.path=maskPath.CGPath;
    button.layer.mask=maskLayer;
    
}
- (void)classFileButtonClick{
    _classFileButton.sakura.backgroundColor(ThemeKP(@"listFileColor"));
    _classFileButton.sakura.titleColor(ThemeKP(@"listFileSelectColor"),UIControlStateNormal);
    _systemFileButton.backgroundColor = [UIColor clearColor];
    _systemFileButton.sakura.titleColor(ThemeKP(@"listFileDefaultColor"),UIControlStateNormal);
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(fileType:)]) {
        [self.delegate fileType:(TKClassFileType)];
    }
    
}
- (void)systemFileButtonClick{
    _systemFileButton.sakura.backgroundColor(ThemeKP(@"listFileColor"));
    _systemFileButton.sakura.titleColor(ThemeKP(@"listFileSelectColor"),UIControlStateNormal);
    
    _classFileButton.backgroundColor = [UIColor clearColor];
    _classFileButton.sakura.titleColor(ThemeKP(@"listFileDefaultColor"),UIControlStateNormal);
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(fileType:)]) {
        [self.delegate fileType:(TKSystemFileType)];
    }
}

//            arrange_down
//            arrange_none
//            arrange_up
- (void)refreshUI{
    for (UIButton *button in _btnArray) {
        switch (button.tag) {
            case TKSortAscending:
                button.sakura.image(ThemeKP(@"arrange_up"),UIControlStateNormal);
                break;
            case TKSortDescending:
                
                button.sakura.image(ThemeKP(@"arrange_down"),UIControlStateNormal);
                break;
            case TKSortNone:
                
                button.sakura.image(ThemeKP(@"arrange_none"),UIControlStateNormal);
                break;
            default:
                break;
        }
    }
}
- (void)nameSortBtnClick:(UIButton *)sender{
    
    for (UIButton *btn in _btnArray) {
        btn.sakura.titleColor(@"TKUserListTableView.coursewareButtonWhiteColor",UIControlStateNormal);
    }
    sender.sakura.titleColor(@"TKUserListTableView.coursewareButtonYellowColor",UIControlStateNormal);
    
    switch (sender.tag) {
        case TKSortAscending:
        {
            sender.tag = TKSortDescending;
            _typeSortBtn.tag = TKSortNone;
            _timeSortBtn.tag = TKSortNone;
            
            if (self.delegate && [self.delegate respondsToSelector:@selector(nameSort:)]) {
                [self.delegate nameSort:TKSortDescending];
                
            }
        }
            break;
        case TKSortDescending:
        case TKSortNone:
        {
            sender.tag = TKSortAscending;
            _typeSortBtn.tag = TKSortNone;
            _timeSortBtn.tag = TKSortNone;
            
            if (self.delegate && [self.delegate respondsToSelector:@selector(nameSort:)]) {
                [self.delegate nameSort:TKSortAscending];
                
            }
        }
            break;
            
        default:
            break;
    }
    [self refreshUI];
    
}

- (void)typeSortBtnClick:(UIButton *)sender{
    for (UIButton *btn in _btnArray) {
        btn.sakura.titleColor(@"TKUserListTableView.coursewareButtonWhiteColor",UIControlStateNormal);
    }
    sender.sakura.titleColor(@"TKUserListTableView.coursewareButtonYellowColor",UIControlStateNormal);
    switch (sender.tag) {
        case TKSortAscending:
        {
            sender.tag = TKSortDescending;
            _nameSortBtn.tag = TKSortNone;
            _timeSortBtn.tag = TKSortNone;
            
            if (self.delegate && [self.delegate respondsToSelector:@selector(typeSort:)]) {
                [self.delegate typeSort:TKSortDescending];
                
            }
        }
            break;
        case TKSortDescending:
        case TKSortNone:
        {
            
            sender.tag = TKSortAscending;
            _nameSortBtn.tag = TKSortNone;
            _timeSortBtn.tag = TKSortNone;
            if (self.delegate && [self.delegate respondsToSelector:@selector(typeSort:)]) {
                [self.delegate typeSort:TKSortAscending];
                
            }
        }
            break;
            
        default:
            break;
    }
    
    [self refreshUI];
}
- (void)timeSortBtnClick:(UIButton *)sender{
    for (UIButton *btn in _btnArray) {
        btn.sakura.titleColor(@"TKUserListTableView.coursewareButtonWhiteColor",UIControlStateNormal);
    }
    sender.sakura.titleColor(@"TKUserListTableView.coursewareButtonYellowColor",UIControlStateNormal);
    switch (sender.tag) {
        case TKSortAscending:
        {
            sender.tag = TKSortDescending;
            _typeSortBtn.tag = TKSortNone;
            _nameSortBtn.tag = TKSortNone;
            
            if (self.delegate && [self.delegate respondsToSelector:@selector(timeSort:)]) {
                [self.delegate timeSort:TKSortDescending];
                
            }
        }
            break;
        case TKSortDescending:
        case TKSortNone:
        {
            sender.tag = TKSortAscending;
            _typeSortBtn.tag = TKSortNone;
            _nameSortBtn.tag = TKSortNone;
            if (self.delegate && [self.delegate respondsToSelector:@selector(timeSort:)]) {
                [self.delegate timeSort:TKSortAscending];
                
            }
        }
            break;
            
        default:
            break;
    }
    
    [self refreshUI];
}

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

@end

