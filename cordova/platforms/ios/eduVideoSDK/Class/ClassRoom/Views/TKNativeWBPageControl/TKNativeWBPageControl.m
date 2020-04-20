//
//  TKNativeWBPageControl.m
//  TKWhiteBoard
//
//  Created by 周洁 on 2018/12/27.
//  Copyright © 2018 MAC-MiNi. All rights reserved.
//

#import "TKNativeWBPageControl.h"
#import "Masonry.h"
#import "UIView+Drag.h"
#import "TKManyViewController.h"
#import "TKNativeWBRemarkView.h"
#import "TKDocmentDocModel.h"

#define LargeNarrowLevelMax 3
#define LargeNarrwoLevelMin 1
#define ThemeKP(args) [@"TKNativeWB.PageControl." stringByAppendingString:args]
#define ProportionToPageControl (IS_PAD ? 1 : 0.8f)

@interface TKNativeWBPageControl()

@property (nonatomic, assign)BOOL  allowPaging;        // 是否允许翻页
@property (nonatomic, assign)BOOL  hidePaging;         // 是否隐藏翻页
@property (nonatomic, assign)TKUserRoleType role;

@end
@implementation TKNativeWBPageControl
{
    TKNativePageTableView *_pageView; // 选页页面
    UIButton *_enlarge;        // 放大
    UIButton *_narrow;        // 缩小
    UIButton *_page;        // 选页按钮
    UIButton *_mark;        // 课件备注
    
    UIImageView *_pageIndicator;
    
    NSInteger _totalPage;
    NSInteger _currentPage;
    
    TKNativeWBRemarkView *_remarkView;
    UIView				 *_contentView;
	
}

/*
 两个配置项：
 1.允许学生翻页 2.禁止学生翻页，两者不可能同时勾选
 当1为YES， 学生可以本地翻页，授权后可以同步翻页（每次翻页会发送信令，其他学生能同步）
 当2为YES， 翻页,跳页直接隐藏, 授权后也不显示
 当1和2为都为No， 学生不可以本地翻页，授权后可以同步翻页（每次翻页会发送信令，其他学生能同步）
 */
- (instancetype)initWithHidePaging:(BOOL)isHide allowPaging:(BOOL)isAllow role:(TKUserRoleType)roleType {
   
    if (self = [super init]) {
        
        self.userInteractionEnabled = YES;
        self.largeNarrowLevel       = 1;
        self.mg_canDrag             = YES;
        self.mg_bounces             = NO;
        self.mg_isAdsorb            = NO;
        self.leftArrow.enabled      = NO;
        // 隐藏翻页
        self.hidePaging        		= NO;
        // 允许翻页
        self.allowPaging    		= YES;
        
        self.role					= roleType;
        
        [self initControl];
        [self layout];
        
        if (self.role == TKUserType_Student) {
            
            // 隐藏翻页
            self.hidePaging		= isHide;
            // 允许翻页
            self.allowPaging    = isAllow;
        }
		else if (self.role == TKUserType_Patrol) {
            // 隐藏翻页
            self.hidePaging		= NO;
            // 允许翻页
            self.allowPaging    = NO;
        }
        
        [self enableButton:self.allowPaging];
    }
    
    return self;
}

- (void)initControl
{
    if (!_leftArrow) {
        _leftArrow = [UIButton buttonWithType:UIButtonTypeCustom];
        _leftArrow.sakura.image(ThemeKP(@"Left"), UIControlStateNormal);
        [_leftArrow addTarget:self action:@selector(prePage) forControlEvents:UIControlEventTouchUpInside];
        _leftArrow.enabled = NO;

        [self addSubview:_leftArrow];
    }
    
    if (!_page) {
        _page = [UIButton buttonWithType:UIButtonTypeCustom];
        _page.titleLabel.lineBreakMode = NSLineBreakByClipping;
        [_page addTarget:self action:@selector(choosePage) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_page];
    }
    
    if (!_rightArrow) {
        _rightArrow = [UIButton buttonWithType:UIButtonTypeCustom];
        _rightArrow.sakura.image(ThemeKP(@"Right"), UIControlStateNormal);
        [_rightArrow addTarget:self action:@selector(nextPage) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_rightArrow];
    }
    
    if (!_enlarge) {
            
            _enlarge = [UIButton buttonWithType:UIButtonTypeCustom];
            _enlarge.sakura.image(ThemeKP(@"EnlargeEnable"), UIControlStateNormal);
            _enlarge.sakura.image(ThemeKP(@"EnlargeDisable"), UIControlStateDisabled);
            [_enlarge addTarget:self action:@selector(enlarge) forControlEvents:UIControlEventTouchUpInside];
            [self addSubview:_enlarge];
    }
        
    if (!_narrow) {
        _narrow = [UIButton buttonWithType:UIButtonTypeCustom];
        _narrow.sakura.image(ThemeKP(@"NarrowEnable"), UIControlStateNormal);
        _narrow.sakura.image(ThemeKP(@"NarrowDisable"), UIControlStateDisabled);
        [_narrow addTarget:self action:@selector(narrow) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_narrow];
    }
    
    if (!_fullScreen) {
        _fullScreen = [UIButton buttonWithType:UIButtonTypeCustom];
        _fullScreen.sakura.image(ThemeKP(@"FullScreen"), UIControlStateNormal);
        _fullScreen.sakura.image(ThemeKP(@"FullScreenExit"), UIControlStateSelected);
        [_fullScreen addTarget:self action:@selector(fullScreen:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_fullScreen];
    }
    
    if (!_pageIndicator) {
        _pageIndicator = [[UIImageView alloc] init];
        _pageIndicator.sakura.image(ThemeKP(@"PageDown"));
        [self addSubview:_pageIndicator];
        _pageIndicator.hidden = [TKRoomManager instance].localUser.role == TKUserType_Patrol;
    }
    
}

- (void)layout {

    CGFloat btnWidth = 34 * ProportionToPageControl;
    CGFloat btnHeight= 34 * ProportionToPageControl;
    
    [_leftArrow mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(btnWidth, btnHeight)]);
        make.top.equalTo(self).offset(0);
        make.bottom.equalTo(self.mas_bottom).offset(0);
        if (!_leftArrow.hidden) {
            make.left.equalTo(self).offset(38 * ProportionToPageControl);
        }
    }];
    

    [_page mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(60 * ProportionToPageControl, 15 * ProportionToPageControl)]);
        if (_leftArrow.hidden) {
            make.left.equalTo(self.mas_left).offset(20 * ProportionToPageControl);
        }else{
            make.left.equalTo(_leftArrow.mas_right).offset(15 * ProportionToPageControl);
        }
        make.centerY.equalTo(self.mas_centerY);
    }];
    
    [_pageIndicator mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(_page.mas_right).offset(0);
        make.centerY.equalTo(_page.mas_centerY);
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(9, 9)]);
    }];
    
    [_rightArrow mas_makeConstraints:^(MASConstraintMaker *make) {
        
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(btnWidth, btnHeight)]);
        make.left.equalTo(_pageIndicator.mas_right).offset(20 * ProportionToPageControl);
        make.centerY.equalTo(self.mas_centerY);
    }];

    [_enlarge mas_remakeConstraints:^(MASConstraintMaker *make) {
        if (_showZoomBtn) {
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(btnWidth, btnHeight)]);
        }
        else {
            make.size.mas_equalTo(0);
        }
        if (_rightArrow.hidden) {
            make.left.equalTo(_page.mas_right).offset(35 * ProportionToPageControl);
        }else{
            make.left.equalTo(_rightArrow.mas_right).offset(35 * ProportionToPageControl);
        }
        make.centerY.equalTo(self.mas_centerY);
    }];
    
    
    [_narrow mas_remakeConstraints:^(MASConstraintMaker *make) {
        if (_showZoomBtn) {
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(btnWidth, btnHeight)]);
        }
        else {
            make.size.mas_equalTo(0);
        }
        make.left.equalTo(_enlarge.mas_right).offset(35 * ProportionToPageControl);
        make.centerY.equalTo(self.mas_centerY);
    }];

    
    [_fullScreen mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(btnWidth, btnHeight)]);
        if (_showZoomBtn) {
            make.left.equalTo(_narrow.mas_right).offset(35 * ProportionToPageControl);
        }
        else {
            if (_rightArrow.hidden) {
                make.left.equalTo(_page.mas_right).offset(35 * ProportionToPageControl);
            }else{
                make.left.equalTo(_rightArrow.mas_right).offset(35 * ProportionToPageControl);
            }
        }
        make.centerY.equalTo(self.mas_centerY);
        make.right.equalTo(self.mas_right).offset(-38 * ProportionToPageControl).priorityLow();
    }];
    
    
    if (self.largeNarrowLevel == LargeNarrowLevelMax) {
        _enlarge.enabled = NO;
    } else if (self.largeNarrowLevel == LargeNarrwoLevelMin) {
        _narrow.enabled  = NO;
    }
    
    [self setNeedsLayout];
    [self layoutIfNeeded];
}


- (void)setShowMark:(BOOL)showMark
{
    _showMark = showMark;
    
    if (showMark) {
        // 课件备注
        if (!_mark) {
            _mark = [UIButton buttonWithType:UIButtonTypeCustom];
            _mark.selected = YES;
            _mark.sakura.image(ThemeKP(@"RemarkDefault"), UIControlStateNormal);
            _mark.sakura.image(ThemeKP(@"RemarkSelected"), UIControlStateSelected);
            [_mark addTarget:self action:@selector(shwoMark:) forControlEvents:UIControlEventTouchUpInside];
 
            _remarkView = [TKNativeWBRemarkView showRemarkViewAddedTo:_contentView pointingAtView:self];
            _remarkView.hidden = YES;
            
        }
    }

}
- (void)setWhiteBoardControl:(id<TKNativeWBPageControlDelegate>)whiteBoardControl {
 
    _whiteBoardControl	= whiteBoardControl;
    
    _contentView		= ((TKManyViewController *)whiteBoardControl).whiteboardBackView;
}

- (void)setRemarkDict:(NSDictionary *)remarkDict
{
    _remarkDict = remarkDict;
    
    //刷新备注
    [self reloadMark];
}

- (void)setShowZoomBtn:(BOOL)showZoomBtn
{
    if (_showZoomBtn != showZoomBtn) {
        
        _showZoomBtn = showZoomBtn;
        // 刷新
        [self layout];
    }
}

- (void)enableButton:(BOOL)isEnable {
    
    if (_hidePaging == YES && _role != TKUserType_Teacher) {
        return;
    }
    
    _leftArrow.enabled   = _currentPage == 1 ? NO : isEnable;    // 上一页
    _rightArrow.enabled  = isEnable;
    _page.enabled        = isEnable;
    _pageView.hidden = YES;
}
- (void)setHidePaging:(BOOL)hidePaging {
    
    if (_hidePaging != hidePaging) {
        
        _hidePaging = hidePaging;
        
        _leftArrow.hidden	= _hidePaging;
        _rightArrow.hidden	= _hidePaging;
        
        _page.enabled		= !_hidePaging;
    }
}


- (void)setCanDraw:(BOOL)canDraw {
    
    if (_role != TKUserType_Student)
        return;
    
    // 隐藏翻页
    if (_hidePaging == YES && _role != TKUserType_Teacher)
        return;
    
    if (_canDraw != canDraw) {
        _canDraw = canDraw;
        if (_canDraw == YES) {
            // 上一页
            _leftArrow.enabled  = _currentPage != 1;
            _rightArrow.enabled = !(_currentPage >= _totalPage);
            _page.enabled = _canDraw;
        }
        else {
//            [self enableButton:_allowPaging];
            [self enableButton:NO];
        }
    }
    
}

- (void)reloadMark
{
    //获取当前备注
    NSString *key = [NSString stringWithFormat:@"%zd",_currentPage];
    __block NSDictionary *dict = nil;
    [self.remarkDict.allValues enumerateObjectsUsingBlock:^(NSDictionary  *_Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:[NSDictionary class]]) {
            NSNumber *pageid = [obj objectForKey:@"pageid"];
            if (pageid.integerValue == key.integerValue) {
                dict = obj;
                *stop = YES;
            }
        }
    }];

    if (dict) {
        _mark.selected = YES;
        _remarkView.hidden = NO;
        _remarkView.remarkContent = [dict valueForKey:@"remark"];

        [self addSubview:_mark];
        [_mark mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.size.equalTo(_fullScreen);
            make.left.equalTo(_fullScreen.mas_right).offset(35 * ProportionToPageControl);
            make.centerY.equalTo(self.mas_centerY);
            make.right.equalTo(self.mas_right).offset(-38 * ProportionToPageControl).priorityHigh();
        }];
        
    }else{
        _mark.selected = NO;;
        _remarkView.hidden = YES;
        [_mark  removeFromSuperview];
    }
}

- (void)updateWithNSDictionary:(NSDictionary *)param {
    
    NSLog(@"updateWithNSDictionary%@", param);
    if (_hidePaging == YES && _role != TKUserType_Teacher)
        return;
    
    if (_allowPaging == YES || _canDraw == YES) {
        
        NSNumber *prevStep = [[param objectForKey:@"page"] objectForKey:@"prevStep"];
        NSNumber *prevPage = [[param objectForKey:@"page"] objectForKey:@"prevPage"];
        NSNumber *nextStep = [[param objectForKey:@"page"] objectForKey:@"nextStep"];
        NSNumber *nextPage = [[param objectForKey:@"page"] objectForKey:@"nextPage"];
        
        if (![prevStep isEqual:[NSNull null]] && !self.leftArrow.enabled) {
            self.leftArrow.enabled = prevPage.boolValue ? : prevStep.boolValue;
        }
        
        if (![nextStep isEqual:[NSNull null]] && !self.rightArrow.enabled) {
            self.rightArrow.enabled = nextPage.boolValue ? : nextStep.boolValue;
        }
        
        
    }

}
- (void)layoutSubviews
{
    [super layoutSubviews];
    
    self.sakura.backgroundColor(ThemeKP(@"BackgroundColor"));
    self.sakura.alpha(ThemeKP(@"BackgroundColorAlpha"));
    self.backgroundColor = [self.backgroundColor colorWithAlphaComponent:self.alpha];
    
    self.layer.masksToBounds = YES;
    self.layer.cornerRadius = self.frame.size.height / 2;
}

#pragma mark - 翻页控制
- (void)prePage
{
    _leftArrow.enabled = _currentPage != 1 ;
 	_rightArrow.enabled = _currentPage < _totalPage;
    
    if (self.whiteBoardControl && [self.whiteBoardControl respondsToSelector:@selector(prePage)]) {
        [self.whiteBoardControl prePage];
    }
    
    _pageView.hidden = YES;
}

- (void)nextPage
{
    _leftArrow.enabled = _currentPage != 1 ;
    _rightArrow.enabled = _currentPage < _totalPage;
    
    if (self.whiteBoardControl && [self.whiteBoardControl respondsToSelector:@selector(nextPage)]) {
        [self.whiteBoardControl nextPage];
    }
    
    _pageView.hidden = YES;
}

- (void)choosePage
{
    _pageView.hidden = !_pageView.hidden;
    _page.sakura.titleColor(_pageView.hidden ? ThemeKP(@"PageColorNormal") : ThemeKP(@"PageColorSelected"), UIControlStateNormal);
    _pageIndicator.sakura.image(_pageView.hidden ? ThemeKP(@"PageDown") : ThemeKP(@"PageUp"));
}

- (void)setTotalPage:(NSInteger)total
         currentPage:(NSInteger)currentPage
{
    
    if (!_pageView) {
        _pageView = [[TKNativePageTableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _pageView.delegate = self;
        _pageView.dataSource = self;
    }
    _pageView.hidden = YES;
    [self.superview addSubview:_pageView];
    
    _totalPage = total;
    if (_totalPage < 1) {
        _totalPage = 1;
    }
    _currentPage = currentPage;
    if (_currentPage < 1) {
        _currentPage = 1;
    }
    
    [_pageView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(_page.mas_centerX);
        make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(85 * Proportion, 45 * Proportion * (_totalPage > 5 ? 5 : _totalPage))]);
        make.bottom.equalTo(self.mas_top).offset(-16 * Proportion);
    }];
    
    [_page setTitle:[NSString stringWithFormat:@"%ld/%ld",(long)_currentPage, (long)_totalPage] forState:UIControlStateNormal];
    _page.sakura.titleColor(_pageView.hidden ? ThemeKP(@"PageColorNormal") : ThemeKP(@"PageColorSelected"), UIControlStateNormal);
    _pageIndicator.sakura.image(_pageView.hidden ? ThemeKP(@"PageDown") : ThemeKP(@"PageUp"));
    [_pageView reloadData];
    [self reloadMark];
    [self setup];
}

- (void)setup
{
    if (_allowPaging || _canDraw) {
        _leftArrow.enabled = _currentPage != 1;
        if (_currentPage == _totalPage) {
            TKDocmentDocModel *model = [TKEduSessionHandle shareInstance].iCurrentDocmentModel;
            // 白板 老师 上课  可以加页
            if ((!model || model.fileid.intValue == 0) &&
                [TKEduSessionHandle shareInstance].isClassBegin &&
                [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                _rightArrow.enabled = YES;
            } else {
                _rightArrow.enabled = NO;
            }
        } else {
            _rightArrow.enabled = YES;
        }
    }
}

- (void)enlarge
{
    if (self.whiteBoardControl && [self.whiteBoardControl respondsToSelector:@selector(enlarge)]) {
        
        if (self.largeNarrowLevel >= LargeNarrowLevelMax) {
            
            _enlarge.enabled = NO;
        } else {
            
            self.largeNarrowLevel += 0.5f;
            _narrow.enabled = YES;
            [self.whiteBoardControl enlarge];
            
            if (self.largeNarrowLevel == LargeNarrowLevelMax) {
                
                _enlarge.enabled = NO;
            }
        }
    }
}

- (void)narrow
{
    if (self.whiteBoardControl && [self.whiteBoardControl respondsToSelector:@selector(narrow)]) {
        
        if (self.largeNarrowLevel <= LargeNarrwoLevelMin) {
            
            _narrow.enabled = NO;
        } else {
         
            self.largeNarrowLevel -= 0.5f;
            _enlarge.enabled = YES;
            [self.whiteBoardControl narrow];
            
            if (self.largeNarrowLevel == LargeNarrwoLevelMin) {
                _narrow.enabled = NO;
            }
        }
    }
}

- (void)resetBtnStates
{
    _enlarge.enabled = YES;
    _narrow.enabled = NO;
    self.largeNarrowLevel = 1;
}

- (void)fullScreen:(UIButton *)btn
{
    if (self.whiteBoardControl && [self.whiteBoardControl respondsToSelector:@selector(fullScreen:)]) {
       
        [self.whiteBoardControl fullScreen:btn.selected];
    }
}

- (void)shwoMark:(UIButton *)btn
{
    if (btn.selected) {
        _remarkView.hidden = YES;
    }else{
        _remarkView.hidden = NO;
    }
    
    btn.selected = !btn.selected;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _totalPage;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 45 * Proportion;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *iden = @"cell";
    TKNativePageCell *cell = [tableView dequeueReusableCellWithIdentifier:iden];
    if (!cell) {
        cell = [[TKNativePageCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:iden];
    }
    [cell setNumber:indexPath.row + 1 selected:NO];
    if ((indexPath.row + 1) == _currentPage) {
        
        [cell setNumber:indexPath.row + 1 selected:YES];
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    _pageView.hidden = YES;
    if (self.whiteBoardControl && [self.whiteBoardControl respondsToSelector:@selector(turnToPage:)]) {
        [self.whiteBoardControl performSelector:@selector(turnToPage:) withObject:@(indexPath.row + 1)];
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

@implementation TKNativePageTableView

- (instancetype)initWithFrame:(CGRect)frame style:(UITableViewStyle)style
{
    if (self = [super initWithFrame:frame style:style]) {
        self.sakura.backgroundColor(ThemeKP(@"BackgroundColor"));
        self.sakura.alpha(ThemeKP(@"BackgroundColorAlpha"));
        self.backgroundColor = [self.backgroundColor colorWithAlphaComponent:self.alpha];
        self.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 5;
        self.delaysContentTouches = NO;
    }
    
    return self;
}

@end

@implementation TKNativePageCell
{
    UILabel *_numberLabel;
}

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.backgroundColor = UIColor.clearColor;
        _numberLabel = [[UILabel alloc] init];
        _numberLabel.textAlignment = NSTextAlignmentCenter;
        _numberLabel.font = [UIFont systemFontOfSize:20 * Proportion];
        [self.contentView addSubview:_numberLabel];
        [_numberLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
    }
    
    return self;
}

- (void)setNumber:(NSInteger)number selected:(BOOL)selected
{
    _numberLabel.text = [NSString stringWithFormat:@"%ld",(long)number];
    _numberLabel.sakura.textColor(selected ? ThemeKP(@"PageColorSelected") : ThemeKP(@"PageColorNormal"));
}

@end
