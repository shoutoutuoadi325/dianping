<!-- 正确的 App.vue -->
<template>
  <!-- 唯一合法的模板根 -->
  <router-view></router-view>
</template>

<script>
export default {
  name: 'App'
}
</script>
<!--&lt;!&ndash; AuthPage.vue &ndash;&gt;-->
<!--<template>-->
<!--  <div class="auth-page">-->
<!--    &lt;!&ndash; 左侧背景图 &ndash;&gt;-->
<!--    <div class="background-section">-->
<!--      <div class="background-overlay"></div>-->
<!--      <img src="@/assets/background.jpg" alt="背景图" class="background-image">-->
<!--    </div>-->

<!--    &lt;!&ndash; 右侧表单区域 &ndash;&gt;-->
<!--    <div class="form-section">-->
<!--      &lt;!&ndash; 登录/注册切换 &ndash;&gt;-->
<!--      <div class="auth-switch">-->
<!--        <h2 :class="{ active: !isLogin }" @click="switchForm('register')">注册</h2>-->
<!--        <h2 :class="{ active: isLogin }" @click="switchForm('login')">登录</h2>-->
<!--      </div>-->

<!--      &lt;!&ndash; 注册表单 &ndash;&gt;-->
<!--      <form v-if="!isLogin" @submit.prevent="handleRegister" class="auth-form">-->
<!--        <div class="form-item" ref="usernameInput">-->
<!--          <input-->
<!--              type="text"-->
<!--              v-model="registerForm.username"-->
<!--              placeholder="用户名"-->
<!--              @input="validUsername"-->
<!--              @blur="validUsername"-->
<!--          >&lt;!&ndash; 直接使用 Font Awesome 图标 &ndash;&gt;-->
<!--          <span class="icon-user"><i class="fas fa-user"></i></span>-->

<!--          <div class="username-wrapper">-->
<!--            <span class="validUsername-text">{{ userNameMessage }}</span>-->
<!--          </div>-->
<!--        </div>-->
<!--        <div class="form-item" ref="passwordInput">-->
<!--          <input-->
<!--              :type="showPassword ? 'text' : 'password'"-->
<!--              v-model="registerForm.password"-->
<!--              placeholder="密码"-->
<!--              @input="checkPasswordStrength"-->
<!--              inputmode="latin"-->
<!--              @compositionstart="disableIME"-->
<!--              @compositionend="disableIME"-->
<!--          >&lt;!&ndash; 使用 Font Awesome 的锁图标 &ndash;&gt;-->
<!--          <span class="icon-lock"><i class="fas fa-lock"></i></span>-->
<!--          &lt;!&ndash; 添加密码显示切换按钮 &ndash;&gt;-->
<!--          <span class="toggle-password" @click="togglePassword">-->
<!--  <i :class="showPassword ? 'fa fa-eye' : 'fa fa-eye-slash'"></i>-->
<!--</span>-->
<!--          &lt;!&ndash; 密码强度显示 &ndash;&gt;-->
<!--          <div class="password-strength-wrapper">-->
<!--            <div class="password-strength">-->
<!--              <div class="strength-bar" :class="strengthClass" :style="{ width: strengthWidth }"></div>-->
<!--            </div>-->
<!--            <span class="strength-text">{{ passwordStrength }}</span>-->
<!--          </div>-->

<!--        </div>-->

<!--        <div class="form-item captcha-item" ref="captchaInput">-->
<!--          <input-->
<!--              v-model="registerForm.captcha"-->
<!--              placeholder="验证码"-->
<!--          >-->
<!--          <img-->
<!--              :src="captchaUrl"-->
<!--              class="captcha-img"-->
<!--              @click="refreshCaptcha"-->
<!--          >-->
<!--          <transition name="fade">-->
<!--            <div v-if="captchaError" class="error-tip">-->
<!--              <i class="fas fa-exclamation-triangle"></i>-->
<!--              {{ captchaError }}-->
<!--            </div>-->
<!--          </transition>-->
<!--        </div>-->

<!--        <button type="submit" class="submit-btn">立即注册</button>-->

<!--        &lt;!&ndash; 用户协议 &ndash;&gt;-->
<!--        <div class="agreement">-->
<!--          点击注册即表示同意-->
<!--          <a href="/agreement">《用户协议》</a>和-->
<!--          <a href="/privacy">《隐私政策》</a>-->
<!--        </div>-->
<!--      </form>-->

<!--      &lt;!&ndash; 登录表单 &ndash;&gt;-->
<!--      <form v-else="isLogin" @submit.prevent="handleLogin" class="auth-form">-->

<!--        <div class="form-item" ref="usernameInput">-->
<!--          <input-->
<!--              type="text"-->
<!--              v-model="loginForm.username"-->
<!--              placeholder="用户名"-->
<!--              @blur="validLoginUsername"-->
<!--          >-->
<!--          &lt;!&ndash; 直接使用 Font Awesome 图标 &ndash;&gt;-->
<!--          <span class="icon-user"><i class="fas fa-user"></i></span>-->
<!--        </div>-->

<!--        <div class="form-item" ref="passwordInput">-->
<!--          <input-->
<!--              :type="showPassword ? 'text' : 'password'"-->
<!--              v-model="loginForm.password"-->
<!--              placeholder="密码"-->
<!--              @input="checkPasswordStrength"-->
<!--              inputmode="latin"-->
<!--              @compositionstart="disableIME"-->
<!--              @compositionend="disableIME"-->
<!--          >-->
<!--          &lt;!&ndash; 使用 Font Awesome 的锁图标 &ndash;&gt;-->
<!--          <span class="icon-lock"><i class="fas fa-lock"></i></span>-->
<!--          &lt;!&ndash; 添加密码显示切换按钮 &ndash;&gt;-->
<!--          <span class="toggle-password" @click="togglePassword">-->
<!--  <i :class="showPassword ? 'fa fa-eye' : 'fa fa-eye-slash'"></i>-->
<!--</span>-->
<!--        </div>-->

<!--        <button type="submit" class="submit-btn">立即登录</button>-->
<!--        &lt;!&ndash; 第三方登录？ &ndash;&gt;-->
<!--      </form>-->


<!--      &lt;!&ndash; 底部链接 &ndash;&gt;-->
<!--      <div class="footer-links">-->
<!--        <a href="/about">关于我们</a>-->
<!--        <a href="/contact">联系我们</a>-->
<!--        <a href="/help">用户帮助</a>-->
<!--        <a href="/partner">合作伙伴</a>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->
<!--<script>-->
<!--export default {-->
<!--  data() {-->
<!--    return {-->
<!--      isLogin: true,-->
<!--      captchaUrl: 'http://localhost:8080/captcha?' + Date.now(),-->
<!--      passwordError: '',-->
<!--      validCaptcha: false,-->
<!--      captchaError: '',-->
<!--      passwordStrengthRules: [-->
<!--        {regex: /.{8,}/, score: 1},       // 长度>=8-->
<!--        {regex: /.{10,}/, score: 1},      //长度>=10-->
<!--        {regex: /.{15,}/, score: 1},      //长度>=15-->
<!--        {regex: /[A-Z]/, score: 1},       // 包含大写字母-->
<!--        {regex: /[0-9]/, score: 1},       // 包含数字-->
<!--        {regex: /[^A-Za-z0-9]/, score: 3} // 包含特殊字符-->
<!--      ],-->
<!--      // 显示密码状态-->
<!--      showPassword: false,-->
<!--      // 修改后的强度等级配置-->
<!--      strengthLevels: [-->
<!--        {class: 'none', text: '密码不可用', width: '0'},-->
<!--        {class: 'weak', text: '密码强度：弱', width: '20%'},-->
<!--        {class: 'medium', text: '密码强度：中', width: '50%'},-->
<!--        {class: 'strong', text: '密码强度：强', width: '80%'},-->
<!--        {class: 'very-strong', text: '密码强度：非常强', width: '100%'}-->
<!--      ],-->
<!--      loginForm: {-->
<!--        username: '',-->
<!--        password: ''-->
<!--      },-->
<!--      registerForm: {-->
<!--        phone: '',-->
<!--        password: '',-->
<!--        captcha: ''-->
<!--      },-->
<!--      passwordStrength: '密码不可用',-->
<!--      strengthClass: 'none', strengthWidth: '0',-->
<!--      userNameMessage: '',-->
<!--    }-->
<!--  },-->
<!--  methods: {-->
<!--    switchForm(type) {-->
<!--      this.isLogin = type === 'login'-->
<!--    },-->
<!--    // 用户名校验方法-->
<!--    async validUsername() {-->
<!--      const username = this.registerForm.username-->

<!--      // 格式校验-->
<!--      if (!/^[a-zA-Z0-9_]{4,20}$/.test(username)) {-->
<!--        this.userNameMessage = '用户名需为4-20位字母、数字或下划线'-->
<!--        return-->
<!--      }-->
<!--      this.userNameMessage = '';-->

<!--      // // 检查用户名是否存在-->
<!--      // try {-->
<!--      //   this.isCheckingUsername = true-->
<!--      //   const res = await this.$http.get(`/user/check?username=${username}`)-->
<!--      //   this.isUsernameAvailable = !res.data.exists-->
<!--      //   if (!this.isUsernameAvailable) {-->
<!--      //     this.userNameMessage = '该用户名已被注册，暂不可用'-->
<!--      //   }-->
<!--      //   this.userNameMessage = '可用＜（＾－＾）＞'-->
<!--      // } catch (err) {-->
<!--      //   this.$message.error('用户名检查失败')-->
<!--      // } finally {-->
<!--      //   this.isCheckingUsername = false-->
<!--      // }-->
<!--    },-->
<!--    togglePassword() {-->
<!--      this.showPassword = !this.showPassword;-->
<!--    },-->
<!--    //登录处理-->
<!--    async handleLogin() {-->
<!--      // 前端基础校验-->
<!--      if (!this.loginForm.username || !this.loginForm.password) {-->
<!--        this.$message.error('用户名和密码不能为空');-->
<!--        return;-->
<!--      }-->

<!--      try {-->
<!--        // 调用登录接口-->
<!--        const res = await this.$http.post('/login', {-->
<!--          username: this.loginForm.username,-->
<!--          password: this.loginForm.password-->
<!--        });-->

<!--        // 保存用户信息到本地存储（根据实际返回字段调整）-->
<!--        localStorage.setItem('userInfo', JSON.stringify(res.data));-->

<!--        // 跳转到首页-->
<!--        this.$router.push('/my'); // 修改这里-->
<!--      } catch (err) {-->
<!--        // 处理错误信息-->
<!--        const errorMsg = err.response?.data?.message || '登录失败';-->
<!--        if (errorMsg.includes('用户不存在')) {-->
<!--          this.$message.error('用户名不存在');-->
<!--        } else if (errorMsg.includes('密码错误')) {-->
<!--          this.$message.error('密码不正确');-->
<!--        } else {-->
<!--          this.$message.error(errorMsg);-->
<!--        }-->
<!--      }-->
<!--    },-->

<!--    // 密码强度计算-->
<!--    checkPasswordStrength() {-->
<!--      const password = this.registerForm.password-->
<!--      this.passwordError = ''-->

<!--      // 基础校验-->
<!--      if (password.length < 6) {-->
<!--        this.passwordStrength = '密码不可用'-->
<!--        this.strengthClass = 'none'-->
<!--        this.strengthWidth = '0'-->
<!--        this.passwordError = '密码长度不能小于6位'-->
<!--        return-->
<!--      }-->
<!--      if (!/\d/.test(password) || !/[a-zA-Z]/.test(password)) {-->
<!--        this.passwordStrength = '密码不可用'-->
<!--        this.strengthClass = 'none'-->
<!--        this.strengthWidth = '0'-->
<!--        this.passwordError = '密码必须同时包含字母和数字'-->
<!--        return-->
<!--      }-->
<!--      this.passwordError = ''-->
<!--      // 强度计算-->
<!--      let score = 0-->
<!--      this.passwordStrengthRules.forEach(rule => {-->
<!--        if (rule.regex.test(password)) score += rule.score-->
<!--      })-->
<!--      // 根据分数确定强度等级-->
<!--      let levelIndex = 0-->
<!--      if (score >= 7) levelIndex = 4      // 非常强-->
<!--      else if (score >= 5) levelIndex = 3  // 强-->
<!--      else if (score >= 3) levelIndex = 2  // 中-->
<!--      else levelIndex = 1-->

<!--      this.strengthClass = this.strengthLevels[levelIndex].class-->
<!--      this.passwordStrength = this.strengthLevels[levelIndex].text-->
<!--      this.strengthWidth = this.strengthLevels[levelIndex].width-->
<!--    },-->
<!--    // 注册处理-->
<!--    async handleRegister() {-->
<!--      // 用户名校验-->
<!--      if (!this.registerForm.username) {-->
<!--        this.userNameMessage = '请输入用户名';-->
<!--        this.triggerShake('username');-->
<!--        return;-->
<!--      }-->
<!--      if (this.userNameMessage) {-->
<!--        this.userNameMessage = '用户名不可用，请检查';-->
<!--        this.triggerShake('username');-->
<!--        return;-->
<!--      }-->

<!--      // 密码强度校验（确保至少达到最低要求）-->
<!--      if (this.strengthClass === 'none' || this.passwordError) {-->
<!--        this.passwordError = '密码不符合安全要求，请按提示修改';-->
<!--        this.triggerShake('password');-->
<!--        return;-->
<!--      }-->

<!--      // 验证码校验-->
<!--      if (!this.registerForm.captcha) {-->
<!--        this.captchaError = '请输入验证码';-->
<!--        this.triggerShake('captcha');-->
<!--        return;-->
<!--      }-->
<!--      try {-->
<!--        // 调用注册接口-->
<!--        const res = await this.$http.post('/users', {-->
<!--          username: this.registerForm.username,-->
<!--          password: this.registerForm.password,-->
<!--          captcha: this.registerForm.captcha-->
<!--        });-->

<!--        // 注册成功后自动登录-->
<!--        localStorage.setItem('userInfo', JSON.stringify(res.data));-->
<!--        // 跳转到首页-->
<!--        window.location.href = '/my';-->
<!--      } catch (err) {-->
<!--        const errorMsg = err.response?.data?.message || '注册失败';-->

<!--        // 特殊处理验证码错误-->
<!--        if (errorMsg.includes('验证码')) {-->
<!--          this.captchaError = '验证码错误';-->
<!--          this.triggerShake('captcha');-->
<!--          this.refreshCaptcha();-->
<!--        }-->
<!--        // 处理用户名已存在-->
<!--        else if (errorMsg.includes('已存在')) {-->
<!--          this.userNameMessage = '用户名已被注册';-->
<!--          this.triggerShake('username');-->
<!--        } // 其他错误处理-->
<!--        else {-->
<!--          this.$message.error(errorMsg);-->
<!--        }-->
<!--      }-->
<!--    },-->
<!--    // 触发震动效果的通用方法-->
<!--    triggerShake(type) {-->
<!--      const refMap = {-->
<!--        username: 'usernameInput',-->
<!--        password: 'passwordInput',-->
<!--        captcha: 'captchaInput'-->
<!--      };-->
<!--      const element = this.$refs[refMap[type]];-->
<!--      if (!element) return;-->
<!--      // 强制清除旧动画-->
<!--      element.style.animation = 'none';-->
<!--      // 触发浏览器重绘-->
<!--      void element.offsetWidth;-->
<!--      // 应用新动画-->
<!--      element.style.animation = 'shake 0.5s cubic-bezier(0.36, 0.07, 0.19, 0.97) both';-->
<!--      // 自动清理动画-->
<!--      setTimeout(() => {-->
<!--        element.style.animation = '';-->
<!--      }, 500);-->
<!--    },-->
<!--    disableIME(event) {-->
<!--      event.target.blur(); // 失去焦点，强制用户重新点击输入框-->
<!--      setTimeout(() => event.target.focus(), 0); // 重新聚焦-->
<!--    },-->
<!--    refreshCaptcha() {-->
<!--      this.captchaUrl = 'http://localhost:8080/captcha?' + Date.now();-->
<!--    },-->

<!--  }-->
<!--}-->
<!--</script>-->
<!--&lt;!&ndash; 在.vue文件的style标签中引入 &ndash;&gt;-->
<!--<style scoped>-->
<!--@import url('@/assets/css/form-style.css');-->
<!--@import url('@/assets/css/error-style.css');-->

<!--/* 组件特有样式可以追加在这里 */-->
<!--</style>-->