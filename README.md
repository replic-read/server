<style>
    .centered {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    #logo-container {
        padding: 16px 0 16px 0;
        width: 100%;
    }
</style>

<picture class="centered" id="logo-container">
  <source srcset="./images/Logo-light.svg" media="(prefers-color-scheme: light)"/>

  <source srcset="./images/Logo-dark.svg"  media="(prefers-color-scheme: dark)"/>

  <img src="./images/Logo-light.svg" alt="Logo"/>
</picture>    

