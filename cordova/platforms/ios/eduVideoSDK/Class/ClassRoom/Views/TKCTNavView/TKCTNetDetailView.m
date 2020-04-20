//
//  TKCTNetDetailView.m
//  EduClass
//
//  Created by talkcloud on 2019/3/20.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import "TKCTNetDetailView.h"
#import "TKEduClassRoom.h"

@interface TKCTNetDetailView ()


@property (nonatomic, assign) CGPoint startPoint;
@property (nonatomic) void(^dissBlock)(void);

@property (nonatomic, strong) UIView * contentView;
@property (nonatomic, strong) UIImageView * sanjiaoImageView;

@property (nonatomic, strong) UILabel * labelRoomID;//    "netstate.RoomID" = "房间号: ";
@property (nonatomic, strong) UILabel * labelPacket;//    "netstate.Packet" = "丢包率: ";
@property (nonatomic, strong) UILabel * labelPing;  //    "netstate.Ping"   = "网络延时: ";

@end

@implementation TKCTNetDetailView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        
        [self addSubview:self.contentView];
        
        _sanjiaoImageView = [[UIImageView alloc] init];
        _sanjiaoImageView.sakura.image(@"ClassRoom.TKNavView.netState_sanjiao");
        [self addSubview:_sanjiaoImageView];
        
        
        _labelRoomID = [[UILabel alloc] init];
        _labelRoomID.font = TITLE_FONT;
        _labelRoomID.textColor = UIColor.whiteColor;
        [self.contentView addSubview:_labelRoomID];
        
        
        _labelPacket = [[UILabel alloc] init];
        _labelPacket.font = TITLE_FONT;
        _labelPacket.textColor = UIColor.whiteColor;
        [self.contentView addSubview:_labelPacket];
        
        
        _labelPing = [[UILabel alloc] init];
        _labelPing.font = TITLE_FONT;
        _labelPing.textColor = UIColor.whiteColor;
        [self.contentView addSubview:_labelPing];
        
        
        [self addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dissView)]];
    }
    return self;
}

- (void)setStartPoint:(CGPoint)startPoint {
    _startPoint = startPoint;
    
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.mas_left).offset(self.startPoint.x);
        make.top.mas_equalTo(self.startPoint.y + 7);
    }];
    
    [_sanjiaoImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.contentView);
        make.bottom.equalTo(self.contentView.mas_top);
        make.size.mas_equalTo(CGSizeMake(19, 7));
    }];
    
    [_labelRoomID mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(10);
        make.left.equalTo(self.contentView).offset(14);
        make.right.equalTo(self.contentView).offset(-24);
        make.height.equalTo(@(30));
    }];
    
    [_labelPacket mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.labelRoomID.mas_bottom);
        make.left.equalTo(self.contentView).offset(14);
        make.height.equalTo(self.labelRoomID);
    }];
    
    [_labelPing mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.labelPacket.mas_bottom);
        make.left.equalTo(self.contentView).offset(14);
        make.height.equalTo(self.labelPacket);
        make.bottom.offset(-10);
    }];
}

- (void) dissView {
    
    [self removeFromSuperview];
    if (self.dissBlock) {
        self.dissBlock();
    }
}

+ (TKCTNetDetailView *) showDetailViewWithPoint:(CGPoint)point diss:(nonnull void (^)(void))block {
    
    TKCTNetDetailView * detailView = [[TKCTNetDetailView alloc] initWithFrame:TKMainWindow.bounds];
    detailView.dissBlock = block;
    detailView.startPoint = point;
    [TKMainWindow addSubview:detailView];
    
    return detailView;
}

- (UIView *)contentView {
    
    if (_contentView == nil) {
        _contentView = [[UIView alloc] init];
        _contentView.layer.cornerRadius = 10 * Proportion;
        _contentView.layer.masksToBounds = YES;
        _contentView.sakura.backgroundColor(@"ClassRoom.TKNavView.netState_bg_color");
    }
    return _contentView;
}

- (void) changeDetailData:(id)state {
    //    "netstate.RoomID" = "房间号: ";
    //    "netstate.Packet" = "丢包率: ";
    //    "netstate.Ping"   = "网络延时: ";
    
    NSString * roomID = [NSString stringWithFormat:@"%@%@", TKMTLocalized(@"netstate.RoomID"), [TKEduClassRoom shareInstance].roomJson.roomid];
    NSString * packetStr = TKMTLocalized(@"netstate.Packet");
    NSString * pingStr = TKMTLocalized(@"netstate.Ping");
    
    if ([state isKindOfClass:[TKAudioStats class]]) {
        
        CGFloat totalP = (CGFloat)[(TKAudioStats *)state totalPackets];
        NSInteger curpack = [(TKAudioStats *)state packetsLost];
        CGFloat packetlv = (totalP > 0) ? (curpack/totalP) : 0.00;
        packetStr = [NSString stringWithFormat:@"%@%.0f%@", TKMTLocalized(@"netstate.Packet"), packetlv * 100, @"%"];
        
        pingStr = [NSString stringWithFormat:@"%@%ldms", TKMTLocalized(@"netstate.Ping"), [(TKAudioStats *)state currentDelay]];
        
    } else {
        
        CGFloat totalP = (CGFloat)[(TKVideoStats *)state totalPackets];
        NSInteger curpack = [(TKVideoStats *)state packetsLost];
        CGFloat packetlv = (totalP > 0) ? (curpack/totalP) : 0.00;
        packetStr = [NSString stringWithFormat:@"%@%.0f%@", TKMTLocalized(@"netstate.Packet"), packetlv * 100, @"%"];
        
        pingStr = [NSString stringWithFormat:@"%@%ldms", TKMTLocalized(@"netstate.Ping"), [(TKVideoStats *)state currentDelay]];
    }
    
    _labelRoomID.text = roomID;
    _labelPacket.text = packetStr;
    _labelPing.text   = pingStr;
}

@end
