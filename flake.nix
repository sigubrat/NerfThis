{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };
  outputs = {
    nixpkgs,
    flake-utils,
    ...
  }:
    flake-utils.lib.eachDefaultSystem (system: let
      javaVersion = 19;

      overlays = [
        (self: super: rec {
          jdk = super."jdk${toString javaVersion}";
          maven = super.maven.override {
            inherit jdk;
          };
        })
      ];
      pkgs = import nixpkgs {inherit overlays system;};
    in {
      devShell = let
        generateEditorConfig = pkgs.writeShellScriptBin "generateEditorConfig" ''
          if [ ! -f .editorconfig ]; then
            echo "root = true" > .editorconfig
            echo "" >> .editorconfig
            echo "[*]" >> .editorconfig
            echo "end_of_line = lf" >> .editorconfig
            echo "insert_final_newline = true" >> .editorconfig
            echo "indent_style = tab" >> .editorconfig
            echo "tab_width = 4" >> .editorconfig
            echo "charset = utf-8" >> .editorconfig
            echo "" >> .editorconfig
            echo "[*.{yaml,yml}]" >> .editorconfig
            echo "indent_style = space" >> .editorconfig
            echo "indent_size = 2" >> .editorconfig
            echo "" >> .editorconfig
            echo "[*.{md,nix}]" >> .editorconfig
            echo "indent_style = space" >> .editorconfig
            echo "indent_size = 2" >> .editorconfig
          fi
        '';
      in
        pkgs.mkShell {
          name = "java";
          buildInputs = with pkgs; [
            jdk
            maven
            tomcat9
            opencv
          ];
          shellHook = ''
            export JAVA_HOME=${pkgs.jdk}
            ${generateEditorConfig}/bin/generateEditorConfig
          '';
        };
    });
}
