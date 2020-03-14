//
//  TKAnswerSheetLayout.m
//  EduClass
//
//  Created by maqihan on 2019/1/7.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetLayout.h"
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@implementation TKAnswerSheetLayout

-(void)prepareLayout
{
    [super prepareLayout];
    
    _cellCount = [[self collectionView] numberOfItemsInSection:0];
}

-(CGSize)collectionViewContentSize
{
    return [self collectionView].frame.size;
}

- (UICollectionViewLayoutAttributes *)layoutAttributesForItemAtIndexPath:(NSIndexPath *)path
{
    UICollectionViewLayoutAttributes* attributes = [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:path];

    CGFloat cellWidth  = self.cellCount > 4 ? CGRectGetWidth(self.collectionView.frame) /2 : CGRectGetWidth(self.collectionView.frame);
    CGFloat cellHeight = Fit(45);
    
    if (path.item < 4) {
        attributes.frame = CGRectMake(0, cellHeight * path.item, cellWidth, cellHeight);
        
    }else{
        attributes.frame = CGRectMake(cellWidth, cellHeight * (path.item - 4), cellWidth, cellHeight);
    }
    
    return attributes;
}

-(NSArray*)layoutAttributesForElementsInRect:(CGRect)rect
{
    NSMutableArray* attributes = [NSMutableArray array];
    for (NSInteger i=0 ; i < self.cellCount; i++) {
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:i inSection:0];
        [attributes addObject:[self layoutAttributesForItemAtIndexPath:indexPath]];
    }
    return attributes;
}

@end
