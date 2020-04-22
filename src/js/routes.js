
import HomePage from '../pages/home.vue';
import SettingsPage from '../pages/settings.vue';
import NotFoundPage from '../pages/404.vue';
import PersonalPage from '../pages/personal.vue';
import histroyClassesPage from '../pages/histroyClasses';
import AboutPage from '../pages/about.vue';
var routes = [

  {
    path: '/',
    component: HomePage,

  },
  {
    path: '/about/',
    component: AboutPage,
  },
  {
    path: '/personal/',
    component: PersonalPage,
  },
  {
    path: '/histroyClasses/',
    component: histroyClassesPage,
  },

  {
    path: '(.*)',
    component: NotFoundPage,
  },
];

export default routes;
