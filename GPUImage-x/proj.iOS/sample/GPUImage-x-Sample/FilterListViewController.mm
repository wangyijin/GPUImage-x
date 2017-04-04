/*
 * GPUImage-x
 *
 * Copyright (C) 2017 Yijin Wang, Yiqian Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "FilterListViewController.h"

@interface FilterListViewController ()

@property (nonatomic, strong) id<FilterListViewDelegate> delegate;

@end

@implementation FilterListViewController

- (instancetype)initWithDelegate:(id <FilterListViewDelegate>)delegate
{
    self = [super init];
    [self setDelegate:delegate];
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"Filter List";
}


#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return NUM_FILTERS;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"FilterListCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"FilterListCell"];
        cell.textLabel.textColor = [UIColor blackColor];
    }
    cell.textLabel.text = [FilterHelper getFilterName:(FilterType)[indexPath row]];
    return cell;
}

#pragma mark - Table view delegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_delegate)
        [_delegate filterSelected:(FilterType)[indexPath row]];
    
    [[self navigationController] popViewControllerAnimated:true];
}
@end
