//
//  TKTrophyView.m
//  EduClass
//
//  Created by lyy on 2018/5/25.
//  Copyright © 2018年 talkcloud. All rights reserved.
//


#import "TKTrophyView.h"
#import "TKEduSessionHandle.h"
#import "TKTrophyButton.h"

#define ThemeKP(args) [@"ClassRoom.Trophy." stringByAppendingString:args]
@interface TKTrophyView()
//{
//
//    CGFloat contentH ;
//}

@property (nonatomic, strong) UIView       *videoView;//视频视图
@property (nonatomic, strong) NSArray      *messageArray;

@end

@implementation TKTrophyView

- (id)initWithFrame:(CGRect)frame chatController:(NSString *)chatController{
    
    if (self = [super initWithFrame:CGRectMake(0, 0, ScreenW, ScreenH)]) {
        // 标题
        self.backImageView.frame = frame;
        self.titleText = TKMTLocalized(@"Title.TrophySend");
        self.closeButton.frame = CGRectMake(CGRectGetWidth(self.backImageView.frame)-self.titleH, 0, self.titleH, self.titleH);
        self.contentImageView.frame = CGRectMake(3, self.titleH, self.backImageView.width - 6, self.backImageView.height - self.titleH - 3);
        self.contentImageView.sakura.image(@"TKBaseView.base_bg_corner_2");
    }
    return self;
}

- (void)touchOutSide{
    
    [self dismissAlert];
    
}

- (void)showOnView:(UIView *)view trophyMessage:(NSArray *)message{
    
    [self show];
    self.messageArray = [NSArray arrayWithArray:message];
    
    
#define Start_X 10.0f           // 第一个按钮的X坐标
#define Btn_Space 5.0f          // 2个按钮之间的横间距
    
    CGFloat contentW = self.contentImageView.width;
    CGFloat contentH = self.contentImageView.height;
    CGFloat btnW =(( contentW- 2*Start_X ) - 3*Btn_Space)/4;
    CGFloat btnH = btnW;

    NSInteger line = message.count / 5 + 1 ;  // 行数
    CGFloat allBtnViewHeight = line * btnH + Btn_Space;
    CGFloat Btn_Y = (contentH - allBtnViewHeight)/2.0;
    
    for (int i = 0 ; i < message.count; i++) {
        
        NSInteger index = i % 4;
        NSInteger page = i / 4;
        
        // 圆角按钮
        TKTrophyButton *button = [TKTrophyButton buttonWithType:(UIButtonTypeCustom)];
        button.frame = CGRectMake(index * (btnW + Btn_Space) + Start_X,
                                  page  * (btnH + Btn_Space) + Btn_Y,
                                  btnW,
                                  btnH);
//        button.sakura.backgroundColor(ThemeKP(@"btnBackColor"));
//        button.layer.masksToBounds = YES;
//        button.layer.cornerRadius = 5;
//        button.layer.borderColor = [TKTheme cgColorWithPath:ThemeKP(@"btnBorderColor")];
//        button.layer.borderWidth = 1;
        
        NSString *pathDir = [[NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) lastObject] stringByAppendingPathComponent:[NSString stringWithFormat:@"companyid%@/trophyid%@/trophyIcon", message[i][@"companyid"],message[i][@"trophyid"]]];
        NSData *imgData = [NSData dataWithContentsOfFile:pathDir];
        
        if (!imgData) {
            
            NSString *imageUrl = [TKUtil optString:message[i] Key:@"trophyIcon"];
            NSString *url = [NSString stringWithFormat:@"%@://%@:%@/%@",sHttp,sHost,sPort,imageUrl];
            imgData = [NSData dataWithContentsOfURL:[NSURL URLWithString:url]];
            
        }

        
        button.imageView.contentMode = UIViewContentModeScaleAspectFit;
        [button setImage: [UIImage imageWithData:imgData] forState:UIControlStateNormal];
        button.tag = i;
        [button addTarget:self action:@selector(sendGif:) forControlEvents:(UIControlEventTouchUpInside)];
        
        [self.contentImageView addSubview:button];
        
    }
    

    
}



- (void)sendGif:(UIButton *)sender{
    
    NSInteger tag = sender.tag;
    NSDictionary *dic = self.messageArray[(int)tag];

    if (self.sendTrophy) {
        [self dismissAlert];
        self.sendTrophy(dic);
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
