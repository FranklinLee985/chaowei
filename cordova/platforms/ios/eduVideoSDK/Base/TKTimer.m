#import "TKTimer.h"

#if TARGET_OS_IPHONE

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 60000 // iOS 6.0 or later
#define NEEDS_DISPATCH_RETAIN_RELEASE 0
#else                                         // iOS 5.X or earlier
#define NEEDS_DISPATCH_RETAIN_RELEASE 1
#endif

#else

#if MAC_OS_X_VERSION_MIN_REQUIRED >= 1080     // Mac OS X 10.8 or later
#define NEEDS_DISPATCH_RETAIN_RELEASE 0
#else
#define NEEDS_DISPATCH_RETAIN_RELEASE 1     // Mac OS X 10.7 or earlier
#endif

#endif

@interface TKTimer ()
@property (nonatomic) NSTimeInterval timeoutDate;
@property (nonatomic) dispatch_source_t timer;
@property (nonatomic) NSTimeInterval timeout;
@property (nonatomic) bool repeat;
@property (nonatomic, copy) dispatch_block_t completion;
@property (nonatomic) dispatch_queue_t queue;

@end
/*
1  
 DBL_EPSILON和 FLT_EPSILON主要用于单精度和双精度的比较当中
 double b = sin(M_PI / 6.0);
 if (fabs(b - 0.5) < DBL_EPSILON)
 x++;
 
2 INT_MAX 返回指定整数类型所能表示的最大值
3 DBL_MAX double的最大值
 
 */
@implementation TKTimer

@synthesize timeoutDate = _timeoutDate;
@synthesize timer = _timer;
@synthesize timeout = _timeout;
@synthesize repeat = _repeat;
@synthesize completion = _completion;
@synthesize queue = _queue;

- (id)initWithTimeout:(NSTimeInterval)timeout repeat:(bool)repeat completion:(dispatch_block_t)completion queue:(dispatch_queue_t)queue
{
    self = [super init];
    if (self != nil)
    {
        _timeoutDate = INT_MAX;
        _timeout = timeout;
        _repeat = repeat;
        self.completion = completion;
        self.queue = queue;
    }
    return self;
}

- (void)dealloc
{
    if (_timer != nil)
    {
        dispatch_source_cancel(_timer);
#if NEEDS_DISPATCH_RETAIN_RELEASE
        dispatch_release(_timer);
#endif
        _timer = nil;
    }
}

- (void)start
{
    _timeoutDate = CFAbsoluteTimeGetCurrent() + kCFAbsoluteTimeIntervalSince1970 + _timeout;
    
    _timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, _queue);
    dispatch_source_set_timer(_timer, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(_timeout * NSEC_PER_SEC)), _repeat ? (int64_t)(_timeout * NSEC_PER_SEC) : DISPATCH_TIME_FOREVER, 0);
    
    dispatch_source_set_event_handler(_timer, ^
    {
        if (self.completion)
            self.completion();
        if (!_repeat)
        {
            [self invalidate];
        }
    });
    dispatch_resume(_timer);
}

- (void)fireAndInvalidate
{
    if (self.completion)
        self.completion();
    
    [self invalidate];
}
-(void)fire{
    if (self.completion)
        self.completion();
}
- (void)invalidate
{
    _timeoutDate = 0;
    if (_timer != nil)
    {
        dispatch_source_cancel(_timer);
#if NEEDS_DISPATCH_RETAIN_RELEASE
        dispatch_release(_timer);
#endif
        _timer = nil;
    }
}

- (bool)isScheduled
{
    return _timer != nil;
}

- (void)resetTimeout:(NSTimeInterval)timeout repeat:(BOOL)repeat
{
    [self invalidate];
    
    _timeout = timeout;
    _repeat = repeat;
    [self start];
}

- (NSTimeInterval)remainingTime
{
    if (_timeoutDate < FLT_EPSILON)
        return DBL_MAX;
    else
        return _timeoutDate - (CFAbsoluteTimeGetCurrent() + kCFAbsoluteTimeIntervalSince1970);
}


@end
