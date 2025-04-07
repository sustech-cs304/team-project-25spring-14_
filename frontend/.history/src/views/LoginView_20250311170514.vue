<template>
  <div class="body">
    <div class="main">
      <div :class="['block', 'block_register', { 'is-txl': isLogin }]">
        <form @submit.prevent="register">
          <h1 class="title">REGISTER</h1>
          <div class="form_icons">
            <img
              class="form_icon"
              src="@/assets/images/weixin2.svg"
              alt="WeChat"
            />
            <img class="form_icon" src="@/assets/images/qq1.svg" alt="QQ" />
          </div>
          <span class="text">or use your email</span>
          <input
            v-model="registerForm.username"
            type="text"
            placeholder="Username"
          />
          <input
            v-model="registerForm.password"
            type="password"
            placeholder="Password"
            :class="{ error: handleRegister }"
          />
          <input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="Confirm Password"
            :class="{ error: handleRegister }"
          />
          <p v-if="handleRegister" class="error-text">Please try again.</p>
          <button type="submit" class="form_button">Register</button>
        </form>
      </div>
      <div :class="['block', 'block_login', { 'is-txl is-z200': isLogin }]">
        <form @submit.prevent="login">
          <h1 class="title">LOGIN</h1>
          <div class="form_icons">
            <img
              class="form_icon"
              src="@/assets/images/weixin2.svg"
              alt="WeChat"
            />
            <img class="form_icon" src="@/assets/images/qq1.svg" alt="QQ" />
          </div>
          <span class="text">or use your email</span>
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="Username"
          />
          <input
            v-model="loginForm.password"
            type="password"
            placeholder="Password"
          />
          <button type="submit" class="form_button">Login</button>
        </form>
      </div>
      <div :class="['switch', { login: isLogin }]">
        <div class="switch_circle"></div>
        <div class="switch_circle switch_circle_top"></div>
        <div class="switch_container">
          <h1>iAlbum</h1>
          <h2>{{ isLogin ? "Welcome Back!" : "Nice to have you" }}</h2>
          <p>
            {{
              isLogin
                ? "If you haven't registered yet, please click the button below to register"
                : "If you have an account, please click the button below to login"
            }}
          </p>
          <div class="form_button" @click="isLogin = !isLogin">
            {{ isLogin ? "Sign Up" : "Sign In" }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
// import { ElMessage } from "element-plus";
// import "element-plus/lib/theme-chalk/index.css";
export default {
  name: "LoginView",
  data() {
    return {
      isLogin: true,
      loginForm: {
        username: "",
        password: "",
      },
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
      },
      handleRegister: false,
    };
  },
  methods: {
    login() {
      console.log(this.loginForm.username, this.loginForm.password);
      window.location.reload();
      axios
        .post(
          "http://127.0.0.1:4523/m1/5913862-5600854-default/user/login",
          this.loginForm
        )
        .then((res) => {
          console.log("Successful login: ", res.data);
        })
        .catch((err) => {
          console.error("Failed to login: ", err);
        });
    },
    register() {
      console.log(
        this.registerForm.username,
        this.registerForm.password,
        this.registerForm.confirmPassword
      );
      //前端检查用户名密码规范
      if (this.registerForm.username.length < 5) {
        alert("用户名长度不能小于5位");
        this.handleRegister = true;
        return;
      } else if (this.registerForm.username.length > 16) {
        alert("用户名长度不能大于16位");
        this.handleRegister = true;
        return;
      }
      if (this.registerForm.password.length < 5) {
        alert("密码长度不能小于5位");
        this.handleRegister = true;
        return;
      } else if (this.registerForm.password.length > 16) {
        alert("密码长度不能大于16位");
        this.handleRegister = true;
        return;
      } else {
        if (this.registerForm.password !== this.registerForm.confirmPassword) {
          alert("两次输入的密码不一致");
          this.handleRegister = true;
          return;
        }
      }
      //发送注册请求
      axios
        .post("http://localhost:8080/user/register", this.registerForm)
        .then((res) => {
          console.log("Successful registration: ", res.data);
        })
        .catch((err) => {
          console.error("Failed to register: ", err);
        });
    },
  },
};
</script>

<style lang="scss" scoped>
.body {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: "Montserrat", sans-serif;
  font-size: 12px;
  background-image: url("@/assets/images/background.jpg");
  background-size: cover;
  background-position: center;
  color: #a0a5a8;
}
.main {
  position: relative;
  width: 1000px;
  min-width: 1000px;
  min-height: 600px;
  height: 600px;
  padding: 25px;
  background-color: #ecf0f3;
  box-shadow: 10px 10px 10px #3b3b3f, -10px -10px 10px #f9f9f9;
  border-radius: 12px;
  overflow: hidden;

  .block {
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    top: 0;
    width: 600px;
    height: 100%;
    padding: 25px;
    background-color: #ecf0f3;
    transition: all 1.25s;

    form {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      width: 100%;
      height: 100%;
      color: #a0a5a8;

      .form_icon {
        object-fit: contain;
        width: 40px;
        height: 40px;
        margin: 0 5px;
        opacity: 0.5;
        transition: 0.15s;

        &:hover {
          opacity: 1;
          transition: 0.15s;
          cursor: pointer;
        }
      }

      .title {
        font-size: 34px;
        font-weight: 700;
        line-height: 3;
        color: #181818;
      }

      .text {
        margin-top: 30px;
        margin-bottom: 12px;
      }

      input {
        width: 350px;
        height: 40px;
        margin: 4px 0;
        padding-left: 25px;
        font-size: 13px;
        letter-spacing: 0.15px;
        border: none;
        outline: none;
        // font-family: 'Montserrat', sans-serif;
        background-color: #ecf0f3;
        transition: 0.25s ease;
        border-radius: 8px;
        box-shadow: inset 2px 2px 4px #d1d9e6, inset -2px -2px 4px #f9f9f9;

        &::placeholder {
          color: #a0a5a8;
        }
      }
      input.error {
        border-color: red;
      }
      .error-text {
        color: red;
        margin-top: 5px;
        font-size: 12px;
      }
    }
  }

  .block_register {
    z-index: 100;
    left: calc(100% - 600px);
  }

  .block_login {
    left: calc(100% - 600px);
    z-index: 0;
  }

  .is-txl {
    left: 0;
    transition: 1.25s;
    transform-origin: right;
  }

  .is-z200 {
    z-index: 200;
    transition: 1.25s;
  }

  .switch {
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    width: 400px;
    padding: 50px;
    z-index: 200;
    transition: 1.25s;
    background-color: #ecf0f3;
    overflow: hidden;
    box-shadow: 4px 4px 10px #d1d9e6, -4px -4px 10px #f9f9f9;
    color: #a0a5a8;

    .switch_circle {
      position: absolute;
      width: 500px;
      height: 500px;
      border-radius: 50%;
      background-color: #ecf0f3;
      box-shadow: inset 8px 8px 12px #d1d9e6, inset -8px -8px 12px #f9f9f9;
      bottom: -60%;
      left: -60%;
      transition: 1.25s;
    }

    .switch_circle_top {
      top: -30%;
      left: 60%;
      width: 300px;
      height: 300px;
    }
    // .switch_container h1,
    // .switch_container h2 {
    //   transition: all 1s ease;
    // }

    .switch_container {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      position: absolute;
      width: 400px;
      padding: 50px 55px;
      transition: 1.25s e;

      h1 {
        font-size: 48px;
        font-weight: 700;
        line-height: 3;
        font-family: "Catamaran", cursive;
        color: #181818;
      }

      h2 {
        font-size: 34px;
        font-weight: 700;
        line-height: 3;
        font-family: "dancing script", cursive;
        color: #181818;
      }

      p {
        font-size: 14px;
        letter-spacing: 0.25px;
        text-align: center;
        line-height: 1.6;
        font-family: "Permenent marker", cursive;
      }
    }
  }

  .login {
    left: calc(100% - 400px);

    .switch_circle {
      left: 0;
    }
  }

  .form_button {
    width: 180px;
    height: 50px;
    border-radius: 25px;
    margin-top: 50px;
    text-align: center;
    line-height: 50px;
    font-size: 14px;
    letter-spacing: 2px;
    background-color: #4b70e2;
    color: #f9f9f9;
    cursor: pointer;
    box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #f9f9f9;
    border: none;
    outline: none;
    background: #4b70e2;

    &:hover {
      box-shadow: 2px 2px 3px 0 rgba(255, 255, 255, 50%),
        -2px -2px 3px 0 rgba(116, 125, 136, 50%),
        inset -2px -2px 3px 0 rgba(255, 255, 255, 20%),
        inset 2px 2px 3px 0 rgba(0, 0, 0, 30%);
    }
  }
}
</style>
