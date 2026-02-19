# Checklist

## Critical 问题验证

- [ ] PlayerManager 协程泄漏已修复，release() 方法可正确取消协程
- [ ] PlayerService 静态变量已移除，使用 StateFlow 传递状态
- [ ] PlayerManager 和 PlayerService 之间的通信已重构

## High 问题验证

- [ ] DeepSeekApiServiceImpl 网络请求使用 withContext(Dispatchers.IO)
- [ ] AiRepository 已在 RepositoryModule 中正确绑定
- [ ] 数据库迁移策略已优化，不再使用 fallbackToDestructiveMigration

## Medium 问题验证

- [ ] HomeViewModel 的 isLoading 状态在所有数据加载完成后才设为 false
- [ ] PlayerScreen Slider 拖动时不会频繁触发 seekTo
- [ ] MusicScanner 进度计算正确显示
- [ ] MediaStoreHelper 中无未使用的导入

## Low 问题验证

- [ ] PlayerScreen 队列高度使用动态计算
- [ ] SongListItem 参数风格统一

## 编译验证

- [ ] 项目编译成功无错误
- [ ] 无新增 Lint 警告
