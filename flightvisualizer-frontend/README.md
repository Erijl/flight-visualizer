# FlightvisualizerFrontend

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 17.2.0.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Building for production

First, create a standalone css build for mapbox-gl:
1. run `git clone https://github.com/mapbox/mapbox-gl-js.git`
2. enter the directory `cd mapbox-gl-js`
3. install the packages `npm install`
4. create the standalone css build via `npm run build-css`

Once those commands finish, you will have a standalone build at and `dist/mapbox-gl.css`

Copy over the css file into the `assets` directory anf continue with the last step.

Run `npm run build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.
