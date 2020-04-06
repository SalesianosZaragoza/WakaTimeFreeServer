import React from "react"
import ReactDOM from "react-dom"
import { StateProvider } from "./StateProvider"

import Desktop from "./components/Desktop/main"
window.onload = () =>{
  const appDom = document.getElementById('app')
  const appJsx = (
    <StateProvider>
      <Desktop />
    </StateProvider>
  );

  ReactDOM.render(
    appJsx, appDom);
  }
