<template>
  <f7-page no-toolbar no-navbar no-swipeback login-screen>
    <f7-login-screen-title>用戶登錄</f7-login-screen-title>
    <f7-list form>
      <f7-list-input
        label="Username"
        type="text"
        placeholder="請輸入用戶名"
        :value="username"
        @input="username = $event.target.value"
      ></f7-list-input>
      <f7-list-input
        label="Password"
        type="password"
        placeholder="請輸入密碼"
        :value="password"
        @input="password = $event.target.value"
      ></f7-list-input>
    </f7-list>
    <f7-list>
      <f7-list-button @click="signIn">登錄</f7-list-button>
	  <f7-list-button @click="signIn">取消</f7-list-button>
      <f7-block-footer> 請輸入用戶名和密碼登錄</f7-block-footer>
    </f7-list>
  </f7-page>
</template>

<script>
  export default {
    data() {
      return {
        username: '',
        password: '',
      };
    },
    methods: {
      signIn() {
        const self = this;
        const app = self.$f7;
        const router = self.$f7router;
        app.dialog.alert(`Username: ${self.username}<br>Password: ${self.password}`, () => {
          router.back();
        });
      },
    },
  };
</script>
