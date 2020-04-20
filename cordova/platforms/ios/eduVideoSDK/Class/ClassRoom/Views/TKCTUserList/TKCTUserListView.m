//
//  TKCTUserListView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTUserListView.h"
#import <QuartzCore/QuartzCore.h>
#import "TKEduSessionHandle.h"

#import "TKCTUserListHeaderView.h"
#import "TKCTUserListFooterView.h"
#import "TKCTUserListTableViewCell.h"


#define userListPageNumber 15
#define ThemeKP(args) [@"TKListView." stringByAppendingString:args]


@interface TKCTUserListView ()<UITableViewDataSource, UITableViewDelegate,TKCTUserListFooterViewDelegate> {
    
    CGFloat _toolHeight; //工具条高度
    CGFloat _bottomHeight;//底部按钮高度
    
    int _startIndex;
    int _currentNum;
    int _totalNum;
}

@property (nonatomic, strong) UIView       * backView;
@property (nonatomic,strong)  NSMutableArray *iFileMutableArray;
@property (nonatomic, strong) TKCTUserListHeaderView *userHeaderView;//用户列表工具栏视图
@property (nonatomic, strong) TKCTUserListFooterView *userFooterView;//用户列表操作栏
@property (nonatomic,retain)  UITableView    *iFileTableView;//展示tableview
@property (nonatomic,assign)  BOOL  isClassBegin;//课堂是否开始
@property (nonatomic, strong) dispatch_source_t timer;//定时器

@end

@implementation TKCTUserListView

- (id)initWithFrame:(CGRect)frame userList:(NSString *)userListController{
    
    if (self = [super initWithFrame:frame]) {
        
        // 通知
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(userListUpadate) name:tkUserListNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateData) name:sDocListViewNotification object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadTableView) name:sEveryoneBanChat object:nil];
        // 初始化
        _totalNum = 0;
        _startIndex = 0;
        _currentNum = 0;

        NSString *colorid = [[NSUserDefaults standardUserDefaults] objectForKey:@"com.tingxins.sakura.current.name"];
        if([colorid isEqualToString:@"tigerlily"]) {
            self.titleLabel.textColor = [UIColor colorWithRed:241/255.0 green:99/255.0 blue:58/255.0 alpha:1.0];
        }
        self.contentImageView.frame = CGRectMake(3, self.titleH, self.backImageView.width - 6, self.backImageView.height - self.titleH - 3);
        self.contentImageView.sakura.image(@"TKBaseView.base_bg_corner_2");
        
        _toolHeight = IS_PAD ? CGRectGetHeight(self.frame)/12.0 : 30;
        _bottomHeight = IS_PAD ? 60 : 40;
        
        [self loadTableView:CGRectMake(0,
                                       0,
                                       CGRectGetWidth(self.frame),
                                       CGRectGetHeight(self.frame))
        ];
        [self show:TKFileListTypeUserList isClassBegin:[TKEduSessionHandle shareInstance].isClassBegin];
        
        [self newUI];
        
    }
    return self;
}

- (void)newUI
{
    UIView *underTitleView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.width, self.titleH)];
    underTitleView.sakura.backgroundColor(ThemeKP(@"courseware_bg_Color"));
    underTitleView.sakura.alpha(ThemeKP(@"courseware_bg_alpha"));
    [self addSubview:underTitleView];
    [self sendSubviewToBack:underTitleView];
    
    [self addSubview:self.titleLabel];
    self.titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.backImageView removeFromSuperview];
    [self.contentImageView removeFromSuperview];
    self.backgroundColor = UIColor.clearColor;
    UIView *backView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.width, self.height)];
    backView.sakura.backgroundColor(ThemeKP(@"courseware_bg_Color"));
    backView.sakura.alpha(ThemeKP(@"courseware_bg_alpha"));
    [self addSubview:backView];
    [self sendSubviewToBack:backView];
}


-(void)loadTableView:(CGRect)frame{
    
    //用户列表头部
    _userHeaderView = [[TKCTUserListHeaderView alloc] init];
    _userHeaderView.frame = CGRectMake(0, self.titleH, CGRectGetWidth(frame), _toolHeight);
    [_userHeaderView setTitleHeight:CGRectGetWidth(frame)];
    _userHeaderView.hidden = YES;
    [self addSubview:_userHeaderView];
    
    
    
    // tableView
    _iFileTableView = [[UITableView alloc] initWithFrame:CGRectMake(0,
                                                                   _userHeaderView.bottomY,
                                                                   CGRectGetWidth(frame),
                                                                   CGRectGetHeight(frame)- CGRectGetMaxY(_userHeaderView.frame) - _bottomHeight)
                                                  style:UITableViewStylePlain];
    _iFileTableView.backgroundColor = [UIColor clearColor];
    _iFileTableView.separatorColor  = [UIColor clearColor];
    _iFileTableView.showsHorizontalScrollIndicator = NO;
    _iFileTableView.delegate   = self;
    _iFileTableView.dataSource = self;
    _isClassBegin = NO;
    _iFileTableView.keyboardDismissMode = UIScrollViewKeyboardDismissModeOnDrag;
    [_iFileTableView registerClass:[TKCTUserListTableViewCell class] forCellReuseIdentifier:@"TKCTUserListTableViewCell"];
    [self addSubview:_iFileTableView];
    
    
    // 底部视图
    _userFooterView = [[TKCTUserListFooterView alloc] init];
    _userFooterView.frame = CGRectMake(0, CGRectGetHeight(frame)-_bottomHeight, CGRectGetWidth(frame), _bottomHeight);
    _userFooterView.hidden = YES;
    _userFooterView.delegate = self;
    [self addSubview:_userFooterView];
    
    tk_weakify(self);
    _userFooterView.nextPage = ^{
        
        if ([TKEduSessionHandle shareInstance].bigRoom) {
            if (weakSelf.timer) {
                dispatch_source_cancel(weakSelf.timer);
                weakSelf.timer = nil;
            }
            
            [weakSelf getBigRoomUsers:YES];
            
            [weakSelf startTimer];
            
        }else{
            [weakSelf getRoomUsers:YES];
        }
    };
    _userFooterView.prePage = ^{
        if ([TKEduSessionHandle shareInstance].bigRoom) {
            
            [weakSelf getBigRoomUsers:NO];
        }else{
            
            [weakSelf getRoomUsers:NO];
        }
    };
    
}

#pragma mark - tableViewDelegate
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    return _iFileMutableArray.count;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    TKCTUserListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TKCTUserListTableViewCell" forIndexPath:indexPath];
    
    TKRoomUser *tRoomUser = [_iFileMutableArray objectAtIndex:indexPath.row];
    cell.roomUser = tRoomUser;
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
    
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if ([TKEduClassRoom shareInstance].roomJson.roomrole == TKUserType_Patrol) {
        return;
    }
    
}

#pragma mark - 显示隐藏 方法
-(void)show:(TKFileListType)aFileListType isClassBegin:(BOOL)isClassBegin{
    
    self.hidden = NO;
    
    _isClassBegin = isClassBegin;
    
    self.userHeaderView.hidden = NO;
    self.userFooterView.hidden = NO;
    
    //    //是否是大并发课堂
    if ([TKEduSessionHandle shareInstance].bigRoom) {
        [self getBigRoomUserNumber];
        [self getBigRoomUsers:YES];
        
        [self startTimer];
        
    }
    else {
        [self getRoomUserNumber];
        [self getRoomUsers:YES];
        
    }
    
}


-(void)hide{
    
    [self.userFooterView destory];
    [self.userFooterView removeFromSuperview];
    self.userFooterView = nil;
    
    if (self.timer) {
        
        dispatch_source_cancel(self.timer);
        self.timer = nil;
    }
    
    _startIndex = 0;
    _totalNum = 0;
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - 通知 更新方法
-(void)updateData{
    
    _iFileMutableArray = [[[TKEduSessionHandle shareInstance] userListExpecPtrlAndTchr] mutableCopy];
    
}

- (void)reloadTableView {
    
    [self updateData];
    [self.iFileTableView reloadData];
}
- (void)userListUpadate{
    
    if ([TKEduSessionHandle shareInstance].bigRoom) {
        
        [self getBigRoomUserNumber];
        _startIndex = (_currentNum-1)*userListPageNumber;
        [self getBigRoomUsers:YES];
        
    }else{
        
        int num = [self getRoomUserNumber];
        int totalPage = [TKHelperUtil returnTotalPageNum:num showPage:userListPageNumber];
        if (_currentNum>totalPage) {
            _currentNum = totalPage;
        }
        _startIndex = (_currentNum-1)*userListPageNumber;
        [self getRoomUsers:YES];
        
    }
}

- (void)userListJumpPageNum:(int)pageNum{
    
    _startIndex = (pageNum-1)*userListPageNumber;
    if ([TKEduSessionHandle shareInstance].bigRoom) {
        
        [self getBigRoomUserNumber];
        [self getBigRoomUsers:YES];
        
    }else{
        
        [self getRoomUserNumber];
        [self getRoomUsers:YES];
    }
}

#pragma mark - 获取房间成员列表
- (void)getRoomUsers:(BOOL)nextPage{
    
    NSArray *array = [[[TKEduSessionHandle shareInstance] userListExpecPtrlAndTchr]mutableCopy];
    //如果成员列表人数为0，则不进行刷新页面
    if (array.count == 0) {
        _iFileMutableArray = [array mutableCopy];
        [_iFileTableView reloadData];
        return;
    }
    if (_startIndex<0) {
        _startIndex = 0;
    }
    int pageNum = userListPageNumber;
    if ((nextPage?(_startIndex + userListPageNumber):(_startIndex - userListPageNumber)) >_totalNum) {
        pageNum = _totalNum- (_startIndex);
    }
    if (pageNum == 0) {
        return;
    }
    int currentpage;
    if (nextPage) {//下一页
        currentpage = [TKHelperUtil returnTotalPageNum:_startIndex showPage:userListPageNumber]+1;
    }else{//上一页
        currentpage = _currentNum-1;
        if(currentpage < 0) {
            return;
        }else{
            _startIndex = (currentpage-1) * userListPageNumber;
        }
    }
    //如果成员列表人数小于等于一页显示的人数就从0开始获取
    if (array.count <= userListPageNumber) {
        _startIndex = 0;
    }
    
    [_userFooterView setCurrentPageNum: currentpage];//设置当前页码
    _currentNum = currentpage;
    
    NSIndexSet *indexes = [NSIndexSet indexSetWithIndexesInRange:
                           NSMakeRange(_startIndex,pageNum)];
    
    _iFileMutableArray = [[array objectsAtIndexes:indexes] mutableCopy];
    
    _startIndex = _startIndex + pageNum;
    
    [_iFileTableView reloadData];
}
#pragma mark - 开启定时器，定时刷新用户列表
- (void)startTimer{
    // 获得队列
    dispatch_queue_t queue = dispatch_get_main_queue();
    
    // 创建一个定时器(dispatch_source_t本质还是个OC对象)
    self.timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, queue);
    
    // 设置定时器的各种属性（几时开始任务，每隔多长时间执行一次）
    // GCD的时间参数，一般是纳秒（1秒 == 10的9次方纳秒）
    // 何时开始执行第一个任务
    // dispatch_time(DISPATCH_TIME_NOW, 1.0 * NSEC_PER_SEC) 比当前时间晚3秒
    dispatch_time_t start = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC));
    uint64_t interval = (uint64_t)(2.0 * NSEC_PER_SEC);
    dispatch_source_set_timer(self.timer, start, interval, 0);
    // 设置回调
    dispatch_source_set_event_handler(self.timer, ^{
        
        NSLog(@"------------%@", [NSThread currentThread]);
        if (_currentNum) {
            if ([TKEduSessionHandle shareInstance].bigRoom) {
                
                [self getBigRoomUserNumber];
                _startIndex = (_currentNum-1)*userListPageNumber;
                [self getBigRoomUsers:YES];
                
            }else{
                
                [self getRoomUserNumber];
                _startIndex = (_currentNum-1)*userListPageNumber;
                [self getRoomUsers:YES];
                
            }
        }
        
    });
    
    
    
    dispatch_resume(self.timer);
}




- (void)dismissAlert
{
    [UIView animateWithDuration:0.3f
                     animations:^{
                         [self hide];
                         CGRect rect = self.frame;
                         self.frame = CGRectMake(ScreenW, rect.origin.y, rect.size.width, rect.size.height);
                         self.backView.alpha = 0;
                         
                     }
                     completion:^(BOOL finished){
                         
                         [self.backView removeFromSuperview];
                         [self removeFromSuperview];
                         if (self.dismissBlock) {
                             self.dismissBlock();
                         }
                         
                         // 清除上下台点击次数
                         [TKEduSessionHandle shareInstance].onPlatformClickTimes = 0;
    }];
}

#pragma mark - 获取大并发房间成员数量
- (void)getBigRoomUserNumber{
    
    [[TKEduSessionHandle shareInstance] sessionHandleGetRoomUserNumberWithRole:@[@(TKUserType_Student),@(TKUserType_Assistant)] callback:^(NSInteger num, NSError *error) {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!error) {
                
                NSLog(@"大并发=========人数%ld",(long)num);
                
                _totalNum = (int)num;
                if (num>0) {
                    _userFooterView.hidden = NO;
                    [_userFooterView setTotalNum:[TKHelperUtil returnTotalPageNum:num showPage:userListPageNumber]];
                    
                }else{
                    _userFooterView.hidden = YES;
                    
                }
            }
        });
    }];
    
}
#pragma mark - 获取大并发房间成员列表
- (void)getBigRoomUsers:(BOOL)nextPage{
    
    //    [_userFooterView setCurrentPageNum:[TKHelperUtil returnTotalPageNum:_startIndex showPage:userListPageNumber]+1];
    
    [[TKEduSessionHandle shareInstance] sessionHandleGetRoomUsersWithRole:@[@(TKUserType_Student),@(TKUserType_Assistant)] startIndex:_startIndex maxNumber:userListPageNumber callback:^(NSArray<TKRoomUser *> * _Nonnull users, NSError *error) {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!error) {
                NSArray *array =  [users mutableCopy];
                //如果成员列表人数为0，则不进行刷新页面
                if (array.count == 0) {
                    _iFileMutableArray = [array mutableCopy];
                    [_iFileTableView reloadData];
                    return;
                }
                if (_startIndex<0) {
                    _startIndex = 0;
                }
                
                int pageNum = userListPageNumber;
                if ((nextPage?(_startIndex + userListPageNumber):(_startIndex - userListPageNumber)) >_totalNum) {
                    pageNum = _totalNum- (_startIndex);
                }
                if (pageNum == 0) {
                    return;
                }
                int currentpage;
                if (nextPage) {//下一页
                    currentpage = [TKHelperUtil returnTotalPageNum:_startIndex showPage:userListPageNumber]+1;
                }else{//上一页
                    currentpage = _currentNum-1;
                    if(currentpage < 0) {
                        return;
                    }else{
                        _startIndex = (currentpage-1) * userListPageNumber;
                    }
                }
                //如果成员列表人数小于等于一页显示的人数就从0开始获取
                //                if (array.count <= userListPageNumber) {
                //                    _startIndex = 0;
                //                }
                
                [_userFooterView setCurrentPageNum: currentpage];//设置当前页码
                _currentNum = currentpage;
                
                //                NSIndexSet *indexes = [NSIndexSet indexSetWithIndexesInRange:
                //                                       NSMakeRange(_startIndex,pageNum)];
                
                _iFileMutableArray = [array mutableCopy];
                
                _startIndex = _startIndex + pageNum;
                
                [_iFileTableView reloadData];
            }
        });
        
        
        
    }];
}
#pragma mark - 获取房间成员数量
- (int)getRoomUserNumber{
    
    int num = (int)[[[TKEduSessionHandle shareInstance] userListExpecPtrlAndTchr] count];
    _totalNum = num;
    if (num>0) {
        _userFooterView.hidden = NO;
        [_userFooterView setTotalNum:[TKHelperUtil returnTotalPageNum:num showPage:userListPageNumber]];
        
    }else{
        _userFooterView.hidden = YES;
        
    }
    
    
    NSString *str = [NSString stringWithFormat:@"    %@(%@)", TKMTLocalized(@"Title.UserList"), @(num)];
    self.titleLabel.text = str;
    return num;
}

- (void)show:(UIView *)view
{
    [view addSubview:self];
    CGRect rect = self.frame;
    self.frame = CGRectMake(ScreenW, rect.origin.y, rect.size.width, rect.size.height);
    [view addSubview:self];
    [view bringSubviewToFront:self];
    
    [UIView animateWithDuration:0.3f animations:^{
        self.frame = CGRectMake(ScreenW - rect.size.width, rect.origin.y, rect.size.width, rect.size.height);
    }];
}

- (void)hidden
{
//    CGRect rect = self.frame;
//    [UIView animateWithDuration:0.2f animations:^{
//        self.frame = CGRectMake(ScreenW, rect.origin.y, rect.size.width, rect.size.height);
//    } completion:^(BOOL finished) {
//        [self removeFromSuperview];
//    }];
    [self dismissAlert];
}

-(void)dealloc{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}


@end
