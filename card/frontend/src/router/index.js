import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/collection',
    name: 'Collection',
    component: () => import('../views/CollectionView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/characters',
    name: 'CharacterSelect',
    component: () => import('../views/CharacterSelectView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/decks',
    name: 'DeckBuilder',
    component: () => import('../views/DeckBuilderView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/shop',
    name: 'Shop',
    component: () => import('../views/ShopView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/battle/prepare',
    name: 'BattlePrepare',
    component: () => import('../views/BattlePrepareView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/levels',
    name: 'LevelSelect',
    component: () => import('../views/LevelSelectView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/battle',
    name: 'Battle',
    component: () => import('../views/BattleView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/pvp/battle',
    name: 'PvpBattle',
    component: () => import('../views/PvpBattleView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/pvp/match',
    name: 'Match',
    component: () => import('../views/MatchView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/battle/result',
    name: 'BattleResult',
    component: () => import('../views/BattleResultView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/records',
    name: 'Records',
    component: () => import('../views/RecordView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('../views/StatisticsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/friends',
    name: 'Friends',
    component: () => import('../views/FriendListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/friends/chat/:friendId',
    name: 'Chat',
    component: () => import('../views/ChatView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
