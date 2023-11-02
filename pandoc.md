
## 常用參數列
```
pandoc --pdf-engine=xelatex -V CJKmainfont="Kaiti TC" --highlight-style tango --toc --listings -H listings-setup.tex -o target/README.pdf title.txt ./README.md ./single/README.md
```

## --pdf-engine

Use the specified engine when producing PDF output. Valid values are pdflatex, lualatex, xelatex, latexmk, tectonic, wkhtmltopdf, weasyprint, pagedjs-cli, prince, context, pdfroff, and typst. 

轉為PDF格式的轉換器，通常都是`xelatex`與`lualatex`居多，少部份會看到用`pdflatex`，其他的在我這個領域不太看得到

## CJKmainfont

ont families for use with xelatex or lualatex: take the name of any system font, using the fontspec package. CJKmainfont uses the xecjk package

markdown內文如有亞洲文字，需要指定字型避免亂碼

可用的選擇可自行在Fonts裡查



![Screen Shot 2023-06-17 at 13.56.31](https://picgo.ap-south-1.linodeobjects.com/20230617/79bc5b6aea4269d02661609b81f5ee44.png)

## --highlight-style

Pandoc will automatically highlight syntax in [fenced code blocks](https://pandoc.org/MANUAL.html#fenced-code-blocks) that are marked with a language name.

The color scheme can be selected using the [`--highlight-style`](https://pandoc.org/MANUAL.html#option--highlight-style) option.

內文的code block如果想要特別上色，就指定特定的theme

想看不同的theme的結果可以參考下列網址

https://www.garrickadenbuie.com/blog/pandoc-syntax-highlighting-examples/

## -H

`-H` *FILE*, `--include-in-header=`*FILE*|*URL*

pandoc可以使用多種plugin，基本上都是在header裡用`usepackage`指出

可以把多種格式的設定放在header裡

例如我常用的

`breaklines=true`

如果不加，若code block裡的程式碼過長，卻不自動分行，那就會有部份的程式碼消失...

我常用的listings-setup.tex 檔案內容如下

```
% Contents of listings-setup.tex
\usepackage{xcolor}

\lstset{
    basicstyle=\ttfamily,
    numbers=left,
    keywordstyle=\color[rgb]{0.13,0.29,0.53}\bfseries,
    stringstyle=\color[rgb]{0.31,0.60,0.02},
    commentstyle=\color[rgb]{0.56,0.35,0.01}\itshape,
    numberstyle=\footnotesize,
    stepnumber=1,
    numbersep=5pt,
    backgroundcolor=\color[RGB]{248,248,248},
    showspaces=false,
    showstringspaces=false,
    showtabs=false,
    tabsize=2,
    captionpos=b,
    breaklines=true,
    breakatwhitespace=true,
    breakautoindent=true,
    escapeinside={\%*}{*)},
    linewidth=\textwidth,
    basewidth=0.5em,
}

```



## 多檔整合

`title.txt ./README.md ./single/README.md`

可以同時讀入多個檔案，並在第一個檔案裡加入文件資訊

例如

```
---
title: Book Example
author: Elliot Chen
rights: Nah
geometry: "margin=1cm"
language: zh-TW
---
```



