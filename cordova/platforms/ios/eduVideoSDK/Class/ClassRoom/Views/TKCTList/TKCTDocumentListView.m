//
//  TKCTDocumentListView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/16.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTDocumentListView.h"
#import "TKCTFileListTableViewCell.h"
#import "TKMediaDocModel.h"
#import "TKDocmentDocModel.h"
#import "UIView+TKExtension.h"
#import "TKCTFileListHeaderView.h"
#import "TKManyViewController+Media.h"
#import "TKHUD.h"

#define ThemeKP(args) [@"TKDocumentListView." stringByAppendingString:args]
#define kMargin 10
@interface TKCTDocumentListView ()<listCTProtocol,TKCTFileListHeaderViewDelegate>
{
    CGFloat _toolHeight;//工具条高度
    CGFloat _bottomHeight;//底部按钮高度
    
    UIView  *_publicFileView;
    UIView *_classFileView;
    UITableView *_publicTableView;
    
    UIImageView *_classArrowImageView;
    UIImageView *_publicArrowImageView;
    
    BOOL _shouldHideClassFile;
}

@property (nonatomic,assign)TKFileListType  iFileListType;
@property (nonatomic,strong)NSMutableArray *iFileMutableArray;
//@property (nonatomic,strong)TKDocmentDocModel *whiteBoardModel;//白板文件
@property (nonatomic,strong)NSMutableArray *iClassFileMutableArray;//课堂文件
@property (nonatomic,strong)NSMutableArray *iSystemFileMutableArray;//公共文件
@property (nonatomic,retain)UITableView    *iFileTableView;//展示tableview
@property (nonatomic,assign)BOOL  isClassBegin;//课堂是否开始
@property (nonatomic,strong)UIButton*  iCurrrentButton;
@property (nonatomic,strong)UIButton*  iPreButton;
@property (nonatomic, strong) TKCTFileListHeaderView *fileListHeaderView;//文档工具栏视图
@property (nonatomic, assign) BOOL filecategory;//文档类型 true 分类   false 未分类
@property (nonatomic, assign) TKFileType switchfileType;

@end

@implementation TKCTDocumentListView
-(instancetype)initWithFrame:(CGRect)frame{
    
    if (self = [super initWithFrame:frame]) {
        self.hidden = YES;
        _toolHeight = IS_PAD?CGRectGetHeight(frame)/12.0:40;
        _bottomHeight = IS_PAD ? 50:40;

        _filecategory = [TKEduClassRoom shareInstance].roomJson.configuration.documentCategoryFlag;
        _switchfileType = TKClassFileType;
        
        [self loadTableView:frame];
        
        [self newUI];
    }
    return self;
}

- (void)newUI
{
    _classFileView              = [[UIView alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(_fileListHeaderView.frame), self.width - 20, 42)];
    _classFileView.tag = 1010;
    _classFileView.sakura.backgroundColor(ThemeKP(@"listFileSectionColor"));
    _classFileView.sakura.alpha(ThemeKP(@"listFileSectionAlpha"));
    // 防止视图alpha对子视图的影响
    _classFileView.backgroundColor = [_classFileView.backgroundColor colorWithAlphaComponent:_classFileView.alpha];
    _classFileView.alpha = 1;
    
    _classFileView.layer.cornerRadius   = 6;
    _classFileView.layer.masksToBounds  = YES;
    [self addSubview:_classFileView];

    UILabel *classFileLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 15, 100, 14)];
    classFileLabel.font = TKFont(14);
    classFileLabel.text = TKMTLocalized(@"Title.ClassroomDocuments");
    classFileLabel.textColor =UIColor.whiteColor;
    [_classFileView addSubview:classFileLabel];
    
    _classArrowImageView = [[UIImageView alloc] initWithFrame:CGRectMake(_classFileView.width - 30, (_classFileView.height - 15) / 2, 15, 15)];
    _classArrowImageView.sakura.image(@"TKDocumentListView.icon_arrow_right");
    [_classFileView addSubview:_classArrowImageView];

    UITapGestureRecognizer *classFileG = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapToHide:)];
    [_classFileView addGestureRecognizer:classFileG];
    
    _iFileTableView.frame = CGRectMake(0, CGRectGetMaxY(_classFileView.frame), self.width - 10, self.height - CGRectGetMaxY(_classFileView.frame) - _classFileView.height);
    
    _fileListHeaderView.takePhotoActionBlock = ^{
        [TKEduSessionHandle shareInstance].updateImageUseType = TKUpdateImageUseType_Document;
        [[NSNotificationCenter defaultCenter] postNotificationName:sTakePhotosUploadNotification object:sTakePhotosUploadNotification];
    };
    _fileListHeaderView.choosePhotoActionblock = ^{
        [TKEduSessionHandle shareInstance].updateImageUseType = TKUpdateImageUseType_Document;
        [[NSNotificationCenter defaultCenter] postNotificationName:sChoosePhotosUploadNotification object:sChoosePhotosUploadNotification];
    };
    
    _publicFileView              = [[UIView alloc] initWithFrame:CGRectMake(10, self.height - 42, self.width - 20, 42)];
    _publicFileView.tag = 2020;
    _publicFileView.sakura.backgroundColor(ThemeKP(@"listFileSectionColor"));
    _publicFileView.sakura.alpha(ThemeKP(@"listFileSectionAlpha"));
    // 防止视图alpha对子视图的影响
    _publicFileView.backgroundColor = [_publicFileView.backgroundColor colorWithAlphaComponent:_publicFileView.alpha];
    _publicFileView.alpha = 1;
    
    _publicFileView.layer.cornerRadius   = 6;
    _publicFileView.layer.masksToBounds  = YES;
    [self addSubview:_publicFileView];
    
    UILabel *publicFileLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 15, 100, 14)];
    publicFileLabel.font = TKFont(14);
    publicFileLabel.text = TKMTLocalized(@"Title.PublicDocuments");
    publicFileLabel.textColor = UIColor.whiteColor;
    [_publicFileView addSubview:publicFileLabel];
    
    _publicArrowImageView = [[UIImageView alloc] initWithFrame:CGRectMake(_publicFileView.width - 30, (_publicFileView.height - 15) / 2, 15, 15)];
    _publicArrowImageView.sakura.image(@"TKDocumentListView.icon_arrow_right");
    [_publicFileView addSubview:_publicArrowImageView];
    
    UITapGestureRecognizer *publicFileG = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapToHide:)];
    [_publicFileView addGestureRecognizer:publicFileG];
    
    _publicTableView = [[UITableView alloc]initWithFrame:CGRectMake(0, CGRectGetMaxY(_publicFileView.frame), self.width - 10, _iFileTableView.height) style:UITableViewStylePlain];
    _publicTableView.backgroundColor = [UIColor clearColor];
    _publicTableView.separatorColor  = [UIColor clearColor];
    _publicTableView.showsHorizontalScrollIndicator = NO;
    _publicTableView.delegate   = self;
    _publicTableView.dataSource = self;
    _publicTableView.keyboardDismissMode = UIScrollViewKeyboardDismissModeOnDrag;
    
    [_publicTableView registerClass:[TKCTFileListTableViewCell class] forCellReuseIdentifier:@"TKCTFileListTableViewCellID"];
    [self addSubview:_publicTableView];
    
    //默认都是展开状态
    _iFileTableView.height = 0;
    _publicFileView.y = CGRectGetMaxY(_iFileTableView.frame) + 5;
    _publicTableView.y = CGRectGetMaxY(_publicFileView.frame);
    _publicTableView.height = 0;
    
    _shouldHideClassFile = NO;
    
}

//当文件类型不做区分时只显示一个table并且没有教师文件公用文件的按钮
- (void)switchUIStyle:(BOOL)shouldSwitch
{
    if (shouldSwitch) {
        _classFileView.hidden = YES;
        _publicFileView.hidden = YES;
        _publicTableView.hidden = YES;
        _iFileTableView.y = _classFileView.y;
        _iFileTableView.height = self.height - _classFileView.y;
    }
}

- (void)tapToHide:(UITapGestureRecognizer *)tapG
{
    [self hideTableView:(tapG.view == _classFileView) ? _iFileTableView : _publicTableView animationWithDuration:0.2f];
}

- (void)hideTableView:(UITableView *)tableView animationWithDuration:(NSTimeInterval)timeInterval
{
    BOOL shouldHidePublicFile = _publicTableView.height > 0;
    if (tableView == _iFileTableView) {
        //收起教室文件列表
        [UIView animateWithDuration:timeInterval animations:^{
            
            // 展开教室列表时，收起公用文件列表
            if (_publicTableView.height > 0) {
                _publicTableView.height = 0;
                _publicArrowImageView.sakura.image(@"TKDocumentListView.icon_arrow_right");
            }
            
                        
            _iFileTableView.height = _shouldHideClassFile ? 0 : _iClassFileMutableArray.count * 60 > (self.height - CGRectGetMaxY(_classFileView.frame) - _classFileView.height) ? (self.height - CGRectGetMaxY(_classFileView.frame) - _classFileView.height) : _iClassFileMutableArray.count * 60;

            if (_iFileTableView.height > 0) {
                _publicFileView.y =  (self.height - CGRectGetMaxY(_iFileTableView.frame) > 5) ? CGRectGetMaxY(_iFileTableView.frame) : CGRectGetMaxY(_classFileView.frame) + 5;
            } else {
                _publicFileView.y = CGRectGetMaxY(_classFileView.frame) + 5;
            }
            _publicTableView.y = CGRectGetMaxY(_publicFileView.frame);
            _publicTableView.height = _shouldHideClassFile ? (_publicTableView.height > 0 ? self.height - CGRectGetMaxY(_publicFileView.frame) : 0) : _publicTableView.height;
            _classArrowImageView.sakura.image(_shouldHideClassFile ? @"TKDocumentListView.icon_arrow_right" : @"TKDocumentListView.icon_arrow_down");
            _shouldHideClassFile = !_shouldHideClassFile;
        }];
    }
    else {
        //收起公用文件列表
        [UIView animateWithDuration:timeInterval animations:^{
            // 展开公用列表时，收起教室文件列表
            if (_iFileTableView.height > 0) {
                _shouldHideClassFile = !_shouldHideClassFile;
                _iFileTableView.height = 0;
                _classArrowImageView.sakura.image(@"TKDocumentListView.icon_arrow_right");
            }
            
            _publicFileView.y = CGRectGetMaxY(_classFileView.frame) + 5;
            _publicTableView.y = CGRectGetMaxY(_publicFileView.frame);
            _publicTableView.height = shouldHidePublicFile ? 0 : ((CGRectGetMaxY(_publicTableView.frame) == self.height) ?  self.height - CGRectGetMaxY(_classFileView.frame) - 5 - _publicFileView.height: self.height - CGRectGetMaxY(_publicFileView.frame));
            _publicArrowImageView.sakura.image(shouldHidePublicFile ? @"TKDocumentListView.icon_arrow_right" : @"TKDocumentListView.icon_arrow_down");
        }];
    }
}

-(void)loadTableView:(CGRect)frame{
    
    //文档、媒体头部视图
    _fileListHeaderView = [[TKCTFileListHeaderView alloc]initWithFrame:CGRectMake(0, 0, CGRectGetWidth(frame), _toolHeight) fileType:_filecategory];
    [self addSubview:_fileListHeaderView];
    _fileListHeaderView.hidden = YES;
    _fileListHeaderView.delegate = self;
    
    _iFileTableView = [[UITableView alloc]initWithFrame:CGRectMake(0, _toolHeight, CGRectGetWidth(frame), CGRectGetHeight(frame)-_toolHeight-40) style:UITableViewStylePlain];
    _iFileTableView.backgroundColor = [UIColor clearColor];
    _iFileTableView.separatorColor  = [UIColor clearColor];
    _iFileTableView.showsHorizontalScrollIndicator = NO;
    _iFileTableView.delegate   = self;
    _iFileTableView.dataSource = self;
    _isClassBegin = NO;
    _iFileTableView.keyboardDismissMode = UIScrollViewKeyboardDismissModeOnDrag;
    
    [_iFileTableView registerClass:[TKCTFileListTableViewCell class] forCellReuseIdentifier:@"TKCTFileListTableViewCellID"];
    [self addSubview:_iFileTableView];
}

- (UIButton *)createCommonButtonWithFrame:(CGRect)frame title:(NSString *)title selector:(SEL)selector
{
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = frame;
    [btn setTitle:title forState:UIControlStateNormal];
    btn.titleLabel.font = [UIFont systemFontOfSize:CGRectGetHeight(btn.frame)/9 + 10];
    
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    btn.sakura.backgroundImage(ThemeKP(@"choose_photo_button_click"),UIControlStateNormal);
    [TKUtil setCornerForView:btn];
    [btn addTarget:self action:selector forControlEvents:UIControlEventTouchUpInside];
    btn.hidden = YES;
    return btn;
}
//382

- (void)reloadData {
    
    [self refreshData:_iFileListType isClassBegin:_isClassBegin];
    
}

#pragma mark tableViewDelegate
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (self.filecategory) {
        if (tableView == _iFileTableView) {
            return _iClassFileMutableArray.count;
        } else {
            return _iSystemFileMutableArray.count;
        }
    } else {
        return _iFileMutableArray.count;
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    UITableViewCell *tCell;
    NSString *tString;
    
    NSMutableArray *tmpArray;
    if (self.filecategory) {
        if (tableView == _iFileTableView) {
            tmpArray = _iClassFileMutableArray;
            _switchfileType = TKClassFileType;
        } else {
            tmpArray = _iSystemFileMutableArray;
            _switchfileType = TKSystemFileType;
        }
    } else {
        tmpArray = _iFileMutableArray;
    }
    
    switch (_iFileListType) {
        case TKFileListTypeAudioAndVideo:
        {
            TKCTFileListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TKCTFileListTableViewCellID" forIndexPath:indexPath];
            cell.delegate = self;
            cell.iIndexPath = indexPath;
            tCell = cell;
            
            //@"影音列表"
            tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.MediaList"),@([[TKEduSessionHandle shareInstance].mediaArray count])];
            
//            TKMediaDocModel *tMediaDocModel = [_iFileMutableArray objectAtIndex:indexPath.row];
            TKMediaDocModel *tMediaDocModel = [tmpArray objectAtIndex:indexPath.row];
            
            [cell configaration:tMediaDocModel withFileListType:TKFileListTypeAudioAndVideo isClassBegin:_isClassBegin];
            if (_switchfileType == TKSystemFileType || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                cell.deleteBtn.hidden = YES;
            }
        }
            break;
        case TKFileListTypeDocument:
        {
            
            TKCTFileListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TKCTFileListTableViewCellID" forIndexPath:indexPath];
            cell.delegate = self;
            cell.iIndexPath = indexPath;
            tCell = cell;
            //文档列表
            // NSString *tString = [NSString stringWithFormat:@"文档列表"];
            tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.DocumentList"),@([[TKEduSessionHandle shareInstance].docmentArray count])];
            
//            TKDocmentDocModel *tMediaDocModel = [_iFileMutableArray objectAtIndex:indexPath.row];
            TKDocmentDocModel *tMediaDocModel = [tmpArray objectAtIndex:indexPath.row];
            
            [cell configaration:tMediaDocModel withFileListType:TKFileListTypeDocument isClassBegin:_isClassBegin];
            if (_switchfileType == TKSystemFileType || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                cell.deleteBtn.hidden = YES;
            }
            
        }
            break;
        default:
            break;
    }
    
    tCell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return tCell;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([TKEduClassRoom shareInstance].roomJson.roomrole == TKUserType_Patrol) {
        return;
    }
    
    TKCTFileListTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    UIButton *aButton = cell.watchBtn;
    
        NSMutableArray *tmpArray;
        if (self.filecategory) {
            if (tableView == _iFileTableView) {
                tmpArray = _iClassFileMutableArray;
            } else {
                tmpArray = _iSystemFileMutableArray;
            }
        } else {
            tmpArray = _iFileMutableArray;
        }
    
    [self watchFile:aButton aIndexPath:indexPath withModel:tmpArray[indexPath.row]];
}

-(void)show:(TKFileListType)aFileListType isClassBegin:(BOOL)isClassBegin{
    
    self.hidden = NO;
    
    [self refreshData:aFileListType isClassBegin:isClassBegin];
    
//    _isShow = YES;
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(updateData) name:sDocListViewNotification object:nil];
}

-(void)hide{
    
    self.hidden = YES;
    self.fileListHeaderView.hidden = YES;
    
//    _isShow = NO;
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void)updateData{
    NSString *tString;
    switch (_iFileListType) {
        case TKFileListTypeAudioAndVideo:
        {
            //@"影音列表"
            tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.MediaList"), @([[TKEduSessionHandle shareInstance].mediaArray count])];
            
            if (_filecategory) {
                
//                switch (_switchfileType) {
//                    case TKClassFileType:
//
//                        _iFileMutableArray = [[[TKEduSessionHandle shareInstance] classMediaArray]mutableCopy];
                        _iClassFileMutableArray = [[[TKEduSessionHandle shareInstance] classMediaArray]mutableCopy];
                
//                        break;
//                    case TKSystemFileType:
                
//                        _iFileMutableArray = [[[TKEduSessionHandle shareInstance] systemMediaArray]mutableCopy];
                        _iSystemFileMutableArray = [[[TKEduSessionHandle shareInstance] systemMediaArray]mutableCopy];
                
//                        break;
//                    default:
//                        break;
//                }
            }else{
                _iFileMutableArray = [[[TKEduSessionHandle shareInstance] mediaArray]mutableCopy];
                
            }
            
        }
            break;
        case TKFileListTypeDocument:
        {
            //@"文档列表"
            tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.DocumentList"), @([[TKEduSessionHandle shareInstance].docmentArray count])];
            if (_filecategory) {
                
//                switch (_switchfileType) {
//                    case TKClassFileType:
//                        _iFileMutableArray = [[[TKEduSessionHandle shareInstance] classDocmentArray]mutableCopy];
                        _iClassFileMutableArray = [[[TKEduSessionHandle shareInstance] classDocmentArray]mutableCopy];
//                        break;
//                    case TKSystemFileType:
//                        _iFileMutableArray = [[[TKEduSessionHandle shareInstance] systemDocmentArray]mutableCopy];
                        _iSystemFileMutableArray = [[[TKEduSessionHandle shareInstance] systemDocmentArray]mutableCopy];
//                        break;
//                    default:
//                        break;
//                }
                
            }else{
                _iFileMutableArray = [[[TKEduSessionHandle shareInstance] docmentArray]mutableCopy];
            }
        }
            break;
            
        default:
            break;
    }
    if(_iFileListType != TKFileListTypeUserList){
        
        [_iFileTableView reloadData];
        [_publicTableView reloadData];
        
        if (_iFileTableView.height > 0) {
            
            [self refreshTableHeight:_iFileTableView];
        }
        if (_publicTableView.height > 0) {
            
            [self refreshTableHeight:_publicTableView];
        }

        
    }
    [self switchUIStyle:!_filecategory];
    if (_filecategory) {
        [self rollUpUnderTable];
    }
}

- (void)refreshTableHeight:(UITableView *)tableView {
    
    if (tableView == _iFileTableView) {
        //收起教室文件列表
        [UIView animateWithDuration:0.2 animations:^{
                        
            _iFileTableView.height = _iClassFileMutableArray.count * 60 > (self.height - CGRectGetMaxY(_classFileView.frame) - _classFileView.height) ? (self.height - CGRectGetMaxY(_classFileView.frame) - _classFileView.height) : _iClassFileMutableArray.count * 60;

            _publicFileView.y = (self.height - CGRectGetMaxY(_iFileTableView.frame) > 5) ? CGRectGetMaxY(_iFileTableView.frame) : CGRectGetMaxY(_classFileView.frame) + 5;
            
            _publicTableView.y = CGRectGetMaxY(_publicFileView.frame);
            
        }];
    }
    else {
        _publicTableView.height =  ((CGRectGetMaxY(_publicTableView.frame) == self.height)
                                    ?  self.height - CGRectGetMaxY(_classFileView.frame) - 5 - _publicFileView.height
                                    : self.height - CGRectGetMaxY(_publicFileView.frame));
        
    }
}
- (void)rollUpUnderTable
{
    return;
    //如果是重新点击课件库媒体库刷新在收起状态下不展开
//    if (_iFileTableView.height == 0 && _iClassFileMutableArray.count > 0) {
//        return;
//    }
//    
//    if (_iClassFileMutableArray.count * 60 < self.height - CGRectGetMaxY(_classFileView.frame) - _classFileView.height) {
//        _iFileTableView.height = _iClassFileMutableArray.count * 60;
//        _publicFileView.y = CGRectGetMaxY(_iFileTableView.frame);
//        _publicTableView.y = CGRectGetMaxY(_publicFileView.frame);
//        _publicTableView.height = self.height - _publicTableView.y;
//    }
}


-(void)refreshData:(TKFileListType)aFileListType isClassBegin:(BOOL)isClassBegin{
    
    _iFileListType    = aFileListType;
    _isClassBegin = isClassBegin;
    NSString *tString;
    
    switch (aFileListType) {
            
        case TKFileListTypeAudioAndVideo:
        {
            if (_filecategory) {
                
//                switch (_switchfileType) {
//                    case TKClassFileType:
                        
//                        _iFileMutableArray = [[[TKEduSessionHandle shareInstance] classMediaArray]mutableCopy];
                        //分类：教室文件
                        _iClassFileMutableArray = [[[TKEduSessionHandle shareInstance] classMediaArray]mutableCopy];
                        
//                        break;
//                    case TKSystemFileType:
//                        _iFileMutableArray = [[[TKEduSessionHandle shareInstance] systemMediaArray]mutableCopy];
                        //分类：公共文件
                        _iSystemFileMutableArray = [[[TKEduSessionHandle shareInstance] systemMediaArray]mutableCopy];
//
//                        break;
//                    default:
//                        break;
//                }
                
                
            }else{
                //未分类
                _iFileMutableArray = [[[TKEduSessionHandle shareInstance] mediaArray]mutableCopy];
                
            }
            self.fileListHeaderView.hidden = NO;
            [_fileListHeaderView hideUploadButton:YES];
            //@"影音列表"
            tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.MediaList"), @([[TKEduSessionHandle shareInstance].mediaArray count])];
            
        }
            break;
        case TKFileListTypeDocument:
        {
            if (_filecategory) {
                
//                switch (_switchfileType) {
//                    case TKClassFileType:
//                        _iFileMutableArray = [[[TKEduSessionHandle shareInstance] classDocmentArray]mutableCopy];
                        //分类：教师文件
                        _iClassFileMutableArray = [[[TKEduSessionHandle shareInstance] classDocmentArray]mutableCopy];
                        
//                        break;
//                    case TKSystemFileType:
//                        _iFileMutableArray = [[[TKEduSessionHandle shareInstance] systemDocmentArray]mutableCopy];
                        //分类：公共文件
                        _iSystemFileMutableArray = [[[TKEduSessionHandle shareInstance] systemDocmentArray]mutableCopy];
                        
//                        break;
//                    default:
//                        break;
//                }
                
                
            }else{
                //未分类
                _iFileMutableArray = [[[TKEduSessionHandle shareInstance] docmentArray]mutableCopy];
                
            }
            self.fileListHeaderView.hidden = NO;
            //@"文档列表"
            [_fileListHeaderView hideUploadButton:NO];
            tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.DocumentList"), @([[TKEduSessionHandle shareInstance].docmentArray count])];
            
        }
            break;
            
        default:
            break;
    }
    
    [_iFileTableView reloadData];
    [_publicTableView reloadData];
    
    [self switchUIStyle:!_filecategory];
    if (_filecategory) {
        [self rollUpUnderTable];
    }
}



#pragma mark - 课件切换
- (void)watchFile:(UIButton *)aButton aIndexPath:(NSIndexPath *)aIndexPath withModel:(id)model
{

    if (self.documentDelegate && [self.documentDelegate respondsToSelector:@selector(watchFile)]) {

        
    }
    if( [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol){
        return;
    }


    NSString *tString;
    switch (_iFileListType) {
        case TKFileListTypeAudioAndVideo:
        {
            if (self.documentDelegate && [self.documentDelegate respondsToSelector:@selector(watchFile)]) {
                //播放音视频收起 课件库
                [self.documentDelegate watchFile];
            }
            
            TKMediaDocModel *tMediaDocModel =  model;//[tmpArray objectAtIndex:aIndexPath.row];
            if ([[NSString stringWithFormat:@"%@",tMediaDocModel.fileid] isEqualToString:[NSString stringWithFormat:@"%@",[TKEduSessionHandle shareInstance].iCurrentMediaDocModel.fileid]]) {
                [[TKEduSessionHandle shareInstance] sessionHandleMediaPause:[TKEduSessionHandle shareInstance].iIsPlaying];
                [TKEduSessionHandle shareInstance].iIsPlaying = ![TKEduSessionHandle shareInstance].iIsPlaying;
                aButton.selected = [TKEduSessionHandle shareInstance].iIsPlaying;
                return;
            }
            aButton.selected = YES;
            
            // 正在播放时，需先停止再播放新媒体
            if ([TKEduSessionHandle shareInstance].isPlayMedia) {
                
                [TKEduSessionHandle shareInstance].isPlayMedia = NO;
                [[TKEduSessionHandle shareInstance] sessionHandleUnpublishMedia:nil];
            }
            
            NSString *tNewURLString2 = [TKUtil absolutefileUrl:tMediaDocModel.swfpath
                                                         webIp:sHost
                                                       webPort:sPort];
            [TKEduSessionHandle shareInstance].iPreMediaDocModel = [TKEduSessionHandle shareInstance].iCurrentMediaDocModel;
            [TKEduSessionHandle shareInstance].iCurrentMediaDocModel = tMediaDocModel;
            BOOL tIsVideo = [TKUtil isVideo:tMediaDocModel.filetype];
            NSString * toID = [TKEduSessionHandle shareInstance].isClassBegin ? sTellAll:  [TKEduSessionHandle shareInstance].localUser.peerID;
            [[TKEduSessionHandle shareInstance] sessionHandlePublishMedia:tNewURLString2
                                                                 hasVideo:tIsVideo
                                                                   fileid:[NSString stringWithFormat:@"%@",tMediaDocModel.fileid]
                                                                 filename:tMediaDocModel.filename
                                                                     toID:toID
                                                                    block:^(NSError *error) {
                                                                        
                                                                    }];
            if ([TKEduSessionHandle shareInstance].iIsPlaying != YES) {
                [TKEduSessionHandle shareInstance].iIsPlaying = YES;
            }

        }
            break;
        case TKFileListTypeDocument:
        {
            //文档列表
            tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.DocumentList"),@([[TKEduSessionHandle shareInstance].docmentArray count])];
            
            
            
            [aButton setSelected:YES];
            
            // 上课后再下课之后点击上课时最后点击的文档
            //            if (aButton == _iPreButton && ![TKEduSessionHandle shareInstance].iIsClassEnd) {
            //                return;
            //            }
            
//            TKDocmentDocModel *tDocmentDocModel = [_iFileMutableArray objectAtIndex:aIndexPath.row];
            TKDocmentDocModel *tDocmentDocModel = model;//[tmpArray objectAtIndex:aIndexPath.row];
            
            if ([TKEduSessionHandle shareInstance].isClassBegin) {
                [[TKEduSessionHandle shareInstance] publishtDocMentDocModel:tDocmentDocModel To:sTellAllExpectSender aTellLocal:YES];
                
            }else{
                
                [[TKEduSessionHandle shareInstance].whiteBoardManager showDocumentWithFile:(TKFileModel *)tDocmentDocModel isPubMsg:NO];

                [TKEduSessionHandle shareInstance].iCurrentDocmentModel = tDocmentDocModel;
                
                [[NSNotificationCenter defaultCenter] postNotificationName:sShowPageBeforeClass object:nil];
            }

            
            _iCurrrentButton = aButton;
            if (_iPreButton) {
                [_iPreButton setSelected:NO];
            }
            
            _iPreButton = _iCurrrentButton;
        }
            break;
            
        default:
            break;
    }
    
    [self reloadData];
}
//涂鸦，删除文件，影音
- (void)deleteFile:(UIButton *)aButton aIndexPath:(NSIndexPath *)aIndexPath withModel:(id)model
{
    if ([TKEduClassRoom shareInstance].roomJson.roomrole == TKUserType_Patrol) {
        return;
    }
    TKAlertView *alert = [[TKAlertView alloc]initForWarningWithTitle:TKMTLocalized(@"Prompt.prompt") contentText:TKMTLocalized(@"Prompt.delClassFile") leftTitle:TKMTLocalized(@"Prompt.Cancel") rightTitle:TKMTLocalized(@"Prompt.OK")];
    [alert show];
    alert.rightBlock = ^{
        
        if (self.documentDelegate && [self.documentDelegate respondsToSelector:@selector(deleteFile)]) {
            [self.documentDelegate deleteFile];
        }
        
        if( [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol){
            return;
        }
        
        NSString *tString;
        switch (_iFileListType) {
            case TKFileListTypeAudioAndVideo:
            {
                // 按钮点击后需要等待网络回调后才可用
                aButton.enabled = NO;
                
                //@"影音列表"
                tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.MediaList"),@([_iFileMutableArray count])];
                
                //            TKMediaDocModel *tMediaDocModel =  [_iFileMutableArray objectAtIndex:aIndexPath.row];
                TKMediaDocModel *tMediaDocModel = (TKMediaDocModel *)model;
                
                [TKEduNetManager delRoomFile:[TKEduClassRoom shareInstance].roomJson.roomid
                                       docid:[NSString stringWithFormat:@"%@",tMediaDocModel.fileid]
                                     isMedia:false
                                       aHost:sHost
                                       aPort:sPort
                                aDelComplete:^int(id  _Nullable response) {
                    
                    
                    BOOL isCurrntDM = [[TKEduSessionHandle shareInstance] isEqualFileId:tMediaDocModel aSecondModel:[TKEduSessionHandle shareInstance].iCurrentMediaDocModel];
                    if (isCurrntDM) {
                        [[TKEduSessionHandle shareInstance]sessionHandleUnpublishMedia:nil];
                    }
                    
                    [[TKEduSessionHandle shareInstance] deleteaMediaDocModel:tMediaDocModel To:sTellAllExpectSender];
                    [[TKEduSessionHandle shareInstance] delMediaArray:tMediaDocModel];
                    _iFileMutableArray = [[[TKEduSessionHandle shareInstance] mediaArray]mutableCopy];
                    
                    // 网络回调完成，按钮可用
                    aButton.enabled = YES;
                    [_iFileTableView reloadData];
                    [_publicTableView reloadData];
                    return 1;
                    
                }aNetError:^int(id  _Nullable response) {
                    
                    // 网络回调完成，按钮可用
                    aButton.enabled = YES;
                    return -1;
                    
                }];
                
            }
                break;
            case TKFileListTypeDocument:
            {
                // 按钮点击后需要等待网络回调后才可用
                aButton.enabled = NO;
                
                //@"文档列表"
                tString = [NSString stringWithFormat:@"%@(%@)", TKMTLocalized(@"Title.DocumentList"),@([_iFileMutableArray count])];
                
                
                //            TKDocmentDocModel *tDocmentDocModel = [_iFileMutableArray objectAtIndex:aIndexPath.row];
                TKDocmentDocModel *tDocmentDocModel = (TKDocmentDocModel *)model;
                
                [TKEduNetManager delRoomFile:[TKEduClassRoom shareInstance].roomJson.roomid
                                       docid:[NSString stringWithFormat:@"%@",tDocmentDocModel.fileid]
                                     isMedia:false
                                       aHost:sHost
                                       aPort:sPort
                                aDelComplete:^int(id  _Nullable response) {
                    
                    [[TKEduSessionHandle shareInstance] deleteDocMentDocModel:tDocmentDocModel To:sTellAll];
                    
                    // 网络回调完成，按钮可用
                    aButton.enabled = YES;
                    [_iFileTableView reloadData];
                    [_publicTableView reloadData];
                    return 1;
                }aNetError:^int(id  _Nullable response) {
                    // 网络回调完成，按钮可用
                    aButton.enabled = YES;
                    return -1;
                }];
            }
                break;
                
            default:
                break;
        }
    };
}


#pragma mark - TKCTFileListHeaderView delegate

-(void)fileType:(TKFileType)type{
    
    _switchfileType = type;
    
    switch (_iFileListType) {
        case TKFileListTypeAudioAndVideo://媒体
            
        {
            switch (type) {
                case TKClassFileType://课堂文件
                    
                    _iFileMutableArray = [[[TKEduSessionHandle shareInstance] classMediaArray]mutableCopy];
                    
                    break;
                case TKSystemFileType://系统文件
                    
                    _iFileMutableArray = [[[TKEduSessionHandle shareInstance] systemMediaArray]mutableCopy];
                    
                    break;
                default:
                    break;
            }
        }
            break;
        case TKFileListTypeDocument://文档
            
        {
            switch (type) {
                case TKClassFileType://课堂文件
                    
                    _iFileMutableArray = [[[TKEduSessionHandle shareInstance] classDocmentArray]mutableCopy];
                    break;
                case TKSystemFileType://系统文件
                    
                    _iFileMutableArray = [[[TKEduSessionHandle shareInstance] systemDocmentArray]mutableCopy];
                    break;
                default:
                    break;
            }
        }
            break;
        default:
            break;
    }
    
    [self.iFileTableView reloadData];
    [_publicTableView reloadData];
    
}
//名称排序
- (void)nameSort:(TKSortFileType)type{
    
    TKDocmentDocModel *whiteBoard;
    NSMutableArray *array = [NSMutableArray array];
    
    __block NSMutableArray *sortArray = [NSMutableArray array];
    
    switch (_iFileListType) {
        case TKFileListTypeAudioAndVideo:
            
            array = [TKEduSessionHandle shareInstance].iMediaMutableArray;
            
            break;
        case TKFileListTypeDocument:
            whiteBoard = [TKEduSessionHandle shareInstance].whiteBoard;
            array = [TKEduSessionHandle shareInstance].iDocmentMutableArray;
            [array removeObjectAtIndex:0];
            break;
        default:
            break;
    }
    
    [TKSortTool sortByNameWithArray:array fileListType:_iFileListType sortWay:type sectionBlock:^(id sectionContent) {
        
    } sortTheValueOfBlock:^(id returnValue) {
        sortArray  = returnValue;
        
        
        switch (_iFileListType) {
            case TKFileListTypeAudioAndVideo:
                [TKEduSessionHandle shareInstance].iMediaMutableArray = sortArray;
                break;
            case TKFileListTypeDocument:
                
                [sortArray insertObject:whiteBoard atIndex:0];
                
                [TKEduSessionHandle shareInstance].iDocmentMutableArray = sortArray;
                break;
                
            default:
                break;
        }
        
    }];
    
    [self refreshData:_iFileListType isClassBegin:_isClassBegin];
}
//类型排序
- (void)typeSort:(TKSortFileType)type{
    
    TKDocmentDocModel *whiteBoard;
    NSMutableArray *array = [NSMutableArray array];
    
    __block NSMutableArray *sortArray = [NSMutableArray array];
    
    switch (_iFileListType) {
        case TKFileListTypeAudioAndVideo:
            
            array = [TKEduSessionHandle shareInstance].iMediaMutableArray;
            
            break;
        case TKFileListTypeDocument:
            whiteBoard = [TKEduSessionHandle shareInstance].whiteBoard;
            array = [TKEduSessionHandle shareInstance].iDocmentMutableArray;
            [array removeObjectAtIndex:0];
            break;
        default:
            break;
    }
    
    [TKSortTool sortByTypeWithArray:array fileListType:_iFileListType sortWay:type sectionBlock:^(id sectionContent) {
        
    } sortTheValueOfBlock:^(id returnValue) {
        sortArray  = returnValue;
        
        
        switch (_iFileListType) {
            case TKFileListTypeAudioAndVideo:
                [TKEduSessionHandle shareInstance].iMediaMutableArray = sortArray;
                break;
            case TKFileListTypeDocument:
                
                [sortArray insertObject:whiteBoard atIndex:0];
                
                [TKEduSessionHandle shareInstance].iDocmentMutableArray = sortArray;
                break;
                
            default:
                break;
        }
    }];
    
    
    [self refreshData:_iFileListType isClassBegin:_isClassBegin];
}
//时间排序
- (void)timeSort:(TKSortFileType)type{
    
    
    TKDocmentDocModel *whiteBoard;
    NSMutableArray *array = [NSMutableArray array];
    
    __block NSMutableArray *sortArray = [NSMutableArray array];
    
    switch (_iFileListType) {
        case TKFileListTypeAudioAndVideo:
            
            array = [TKEduSessionHandle shareInstance].iMediaMutableArray;
            
            break;
        case TKFileListTypeDocument:
            whiteBoard = [TKEduSessionHandle shareInstance].whiteBoard;
            
            array = [TKEduSessionHandle shareInstance].iDocmentMutableArray;
            [array removeObjectAtIndex:0];
            break;
        default:
            break;
    }
    
    
    [TKSortTool sortByTimeWithArray:array fileListType:_iFileListType sortWay:type sectionBlock:^(id sectionContent) {
        
    } sortTheValueOfBlock:^(id returnValue) {
        sortArray  = returnValue;
        
        switch (_iFileListType) {
            case TKFileListTypeAudioAndVideo:
                [TKEduSessionHandle shareInstance].iMediaMutableArray = sortArray;
                break;
            case TKFileListTypeDocument:
                
                [sortArray insertObject:whiteBoard atIndex:0];
                
                [TKEduSessionHandle shareInstance].iDocmentMutableArray = sortArray;
                break;
                
            default:
                break;
        }
        for (TKDocmentDocModel *doc in sortArray) {
            NSLog(@"%@",doc.fileid);
        }
    }];
    
    [self refreshData:_iFileListType isClassBegin:_isClassBegin];
}

-(void)dealloc{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end

